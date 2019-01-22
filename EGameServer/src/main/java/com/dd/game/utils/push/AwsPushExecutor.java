package com.dd.game.utils.push;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class AwsPushExecutor {
    private static final int THREAD_COUNT = 5;
    private static final Executor executor = Executors.newFixedThreadPool(THREAD_COUNT,
            new PushThreadFactory("SNS-PUSH"));

    private AwsPushExecutor() {
    }

    public static int getThreadCount() {
        return THREAD_COUNT;
    }

    public static void runInBackground(Runnable runnable) {
        executor.execute(runnable);
    }

    public static Executor getExecutor() {
        return executor;
    }

    private static class PushThreadFactory implements ThreadFactory {

        private String name;
        private AtomicInteger threadNum = new AtomicInteger(1);

        public PushThreadFactory(String name) {
            super();
            this.name = name;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(name + "-" + threadNum.getAndIncrement());
            return t;
        }

    }
}
