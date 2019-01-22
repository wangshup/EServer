package com.dd.gate.utils;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.dd.gate.utils.ECacheBuilder.ICacheable;
import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * 带有弱引用队列的google LoadingCache 当LoadingCache把对象移除时，对象先进入弱引用Map中，
 * 1、如果该对象还有其他的强引用时，则对象会一直保留在Map中，
 * 当再次调用LoadingCache获取该对象时，对象会直接从Map移到LoadingCache中， 无需重新加载，同时保证虚拟机中该对象唯一
 * 2、如果该对象没有其他任何的强引用，则对象会在虚拟机下次内存回收时从Map中移除掉 *
 * 
 * @author wangshupeng
 *
 * @param <K>
 * @param <V>
 */
public final class ECacheBuilder<K, V extends ICacheable<K>> {
    private CacheBuilder<Object, Object> cacheBuilder;
    private final ReferenceQueue<V> queue = new ReferenceQueue<V>();
    private Map<K, ValueReference<K, V>> valueRefrence = new ConcurrentHashMap<>();

    private ECacheBuilder() {
        cacheBuilder = CacheBuilder.newBuilder();
    }

    public static <K, V extends ICacheable<K>> ECacheBuilder<K, V> newBuilder() {
        return new ECacheBuilder<K, V>();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <K1 extends K, V1 extends ICacheable<K1>> LoadingCache<K1, V1> build(CacheLoader<? super K1, V1> loader) {
        return cacheBuilder.build(new ECacheLoader(loader, this));
    }

    public ECacheBuilder<K, V> initialCapacity(int initialCapacity) {
        cacheBuilder.initialCapacity(initialCapacity);
        return this;
    }

    public ECacheBuilder<K, V> expireAfterAccess(long duration, TimeUnit unit) {
        cacheBuilder.expireAfterAccess(duration, unit);
        return this;
    }

    public ECacheBuilder<K, V> expireAfterWrite(long duration, TimeUnit unit) {
        cacheBuilder.expireAfterWrite(duration, unit);
        return this;
    }

    public ECacheBuilder<K, V> maximumSize(long size) {
        cacheBuilder.maximumSize(size);
        return this;
    }

    public ECacheBuilder<K, V> maximumWeight(long weight) {
        cacheBuilder.maximumSize(weight);
        return this;
    }

    public ECacheBuilder<K, V> recordStats() {
        cacheBuilder.recordStats();
        return this;
    }

    public ECacheBuilder<K, V> concurrencyLevel(int concurrencyLevel) {
        cacheBuilder.concurrencyLevel(concurrencyLevel);
        return this;
    }

    public ECacheBuilder<K, V> refreshAfterWrite(long duration, TimeUnit unit) {
        cacheBuilder.refreshAfterWrite(duration, unit);
        return this;
    }

    public ECacheBuilder<K, V> ticker(Ticker ticker) {
        cacheBuilder.ticker(ticker);
        return this;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <K1 extends K, V1 extends ICacheable<K1>> ECacheBuilder<K1, V1> removalListener(
            RemovalListener<? super K1, ? super V1> listener) {
        cacheBuilder.removalListener(new ERemovalListener(listener, this));
        return (ECacheBuilder<K1, V1>) this;
    }

    @SuppressWarnings("rawtypes")
    private void cleanReference() {
        for (Object ref; (ref = queue.poll()) != null;) {
            valueRefrence.remove(((ValueReference) ref).getK());
        }
    }

    public interface ICacheable<K> {
        K getK();
    }

    public static class ValueReference<K, V extends ICacheable<K>> extends WeakReference<V> {
        private K k;

        ValueReference(V value, ReferenceQueue<? super V> q) {
            super(value, q);
            this.k = value.getK();
        }

        K getK() {
            return k;
        }
    }

    static class ERemovalListener<K, V extends ICacheable<K>> implements RemovalListener<K, V> {
        RemovalListener<K, V> listener;
        ECacheBuilder<K, V> builder;

        ERemovalListener(RemovalListener<K, V> listener, ECacheBuilder<K, V> builder) {
            this.listener = listener;
            this.builder = builder;
        }

        @Override
        public void onRemoval(RemovalNotification<K, V> notification) {
            builder.valueRefrence.put(notification.getKey(),
                    new ValueReference<>(notification.getValue(), builder.queue));
            builder.cleanReference();
            listener.onRemoval(notification);
        }
    }

    static class ECacheLoader<K, V extends ICacheable<K>> extends CacheLoader<K, V> {
        private CacheLoader<K, V> loader;
        ECacheBuilder<K, V> builder;

        ECacheLoader(CacheLoader<K, V> loader, ECacheBuilder<K, V> builder) {
            this.loader = loader;
            this.builder = builder;
        }

        public V load(K key) throws Exception {
            builder.cleanReference();
            Reference<V> ref = builder.valueRefrence.remove(key);
            V value = null;
            if (ref != null && (value = ref.get()) != null) {
                return value;
            }
            return loader.load(key);
        }
    }
}
