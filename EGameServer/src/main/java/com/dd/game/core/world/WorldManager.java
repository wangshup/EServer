package com.dd.game.core.world;


import com.dd.game.core.ThreadPoolManager;
import com.dd.game.core.world.march.WorldMarchManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: server
 * @description: 世界管理类
 * @author: wangshupeng
 * @create: 2018-11-12 16:10
 **/
public class WorldManager {

    private static final Logger logger = LoggerFactory.getLogger(WorldManager.class);

    //世界地图点管理
    private static WorldMapManager worldMapManager = createProxy(WorldMapManager.class);

    //世界行军管理
    private static WorldMarchManager worldMarchManager = createProxy(WorldMarchManager.class);

    private static ExecutorService es = new ThreadPoolExecutor(1, 1, //fixed 1 theads
            0L, TimeUnit.MILLISECONDS,//
            new LinkedBlockingQueue<>(),//
            new ThreadPoolManager.ZoneThreadFactory("World-Executor"));

    private static Thread t = null;

    public static final WorldMapManager getWorldMapManager() {
        return worldMapManager;
    }

    public static final WorldMarchManager getWorldMarchManager() {
        return worldMarchManager;
    }

    public static final WorldManager getInstance() {
        return InsHolder.manager;
    }

    static class InsHolder {
        private static final WorldManager manager = createProxy(WorldManager.class);
    }

    private static <T> T createProxy(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new TargetInterceptor());
        return (T) enhancer.create();
    }

    static class TargetInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            Thread thread = Thread.currentThread();
            if (t == null || !t.isAlive()) {
                t = getExecutorThread();
            }
            if (t == thread) {
                return proxy.invokeSuper(obj, args);
            } else {
                AtomicReference<Throwable> exception = new AtomicReference<>();
                Object ret = es.submit(() -> {
                    try {
                        return proxy.invokeSuper(obj, args);
                    } catch (Throwable t) {
                        exception.set(t);
                    }
                    return null;
                }).get(30, TimeUnit.SECONDS);
                if (exception.get() != null) {
                    throw exception.get();
                }
                return ret;
            }
        }
    }

    public static void init() throws Exception {
        try {
            worldMapManager.init();
            worldMarchManager.init();
        } catch (TimeoutException te) {
            logger.warn("World init exceed 30 seconds!!!");
        }
    }

    public static void execute(Runnable task) {
        es.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("World execute task error!", e);
            }
        });
    }

    private static Thread getExecutorThread() {
        try {
            es.submit(() -> {
                return;
            }).get();

            Field f = ThreadPoolExecutor.class.getDeclaredField("workers");
            f.setAccessible(true);
            Set<?> workers = (Set<?>) f.get(es);
            Object worker = workers.iterator().next();
            String name = worker.getClass().getName();
            Field tf = Class.forName(name).getDeclaredField("thread");
            tf.setAccessible(true);
            return (Thread) tf.get(worker);
        } catch (Exception e) {
            logger.error("create world manager error!", e);
        }
        return null;
    }
}
