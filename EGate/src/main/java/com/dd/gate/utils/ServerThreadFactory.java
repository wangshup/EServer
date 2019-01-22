package com.dd.gate.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public ServerThreadFactory(String name) {
        group = new ThreadGroup(name);
        namePrefix = name + "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

    public static boolean isSameGroup(Thread x, Thread y) {
        return x.getThreadGroup() == y.getThreadGroup();
    }

    public static boolean isSameGroup(Thread x, ServerThreadFactory y) {
        return x.getThreadGroup() == y.group;
    }
}
