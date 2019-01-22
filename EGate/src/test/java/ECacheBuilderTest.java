import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.dd.gate.utils.ECacheBuilder;
import com.dd.gate.utils.ECacheBuilder.ICacheable;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class ECacheBuilderTest {

    private TestValue[] tv = new TestValue[10];

    @Test
    public void test() {
        LoadingCache<Long, TestValue> loadingCache = ECacheBuilder.newBuilder().maximumSize(5)
                .expireAfterAccess(10, TimeUnit.SECONDS).expireAfterWrite(10, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<Long, TestValue>() {
                    @Override
                    public void onRemoval(RemovalNotification<Long, TestValue> notification) {
                    }

                }).build(new CacheLoader<Long, TestValue>() {
                    @Override
                    public TestValue load(Long key) throws Exception {
                        return new TestValue(key);
                    }
                });
        try {
            for (int i = 0; i < 10; ++i) {
                System.out.println(tv[i] = loadingCache.get((long) i));
            }
            Thread.sleep(15 * 1000);
            System.gc();
            System.out.println("");
            System.out.println("");
            System.out.println("");
            for (long i = 0; i < 10; ++i) {
                System.out.println(loadingCache.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static class TestValue implements ICacheable<Long> {
        private long k;

        public TestValue(long k) {
            this.k = k;
        }

        @Override
        public Long getK() {
            return k;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TestValue [k=").append(k).append("]===").append(this.hashCode());
            return builder.toString();
        }
    }
}
