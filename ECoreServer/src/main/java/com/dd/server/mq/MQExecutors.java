package com.dd.server.mq;


import com.dd.server.services.ExecutorsService;
import com.dd.server.utils.ServerThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @program: Immortal
 * @description: mq executors
 * @author: wangshupeng
 * @create: 2019-02-21 10:34
 **/
public class MQExecutors {
    private ExecutorService[] executorServices;
    private ExecutorsService.ExecutorChooser.IExecutorChooser executorChooser;

    public MQExecutors(int threadCount, String name) {
        executorServices = new ExecutorService[threadCount];
        ThreadFactory tf = new ServerThreadFactory(name);
        for (int i = 0; i < threadCount; ++i) {
            executorServices[i] = Executors.newSingleThreadExecutor(tf);
        }
        if (ExecutorsService.ExecutorChooser.isPowerOfTwo(threadCount)) {
            executorChooser = new ExecutorsService.ExecutorChooser.PowerOfTwoExecutorChooser(executorServices);
        } else {
            executorChooser = new ExecutorsService.ExecutorChooser.GenericExecutorChooser(executorServices);
        }
    }

    public ExecutorService getExecutor(long id) {
        return executorChooser.next(id);
    }

    public void shutdown() {
        for (ExecutorService es : executorServices) {
            es.shutdown();
        }
    }
}