package com.dd.gate.services;

import com.dd.gate.exceptions.ServiceInitException;
import com.dd.gate.exceptions.ServiceStartException;
import com.dd.gate.exceptions.ServiceStopException;
import com.dd.gate.services.ExecutorsService.ExecutorChooser.GenericExecutorChooser;
import com.dd.gate.services.ExecutorsService.ExecutorChooser.IExecutorChooser;
import com.dd.gate.services.ExecutorsService.ExecutorChooser.PowerOfTwoExecutorChooser;
import com.dd.gate.utils.ServerThreadFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorsService extends AbstractService implements IExecutorService {
    public ScheduledExecutorService scheduleExecutor;
    private ExecutorService[] executorServices;
    private IExecutorChooser executorChooser;

    public ExecutorsService() {
        super(ServiceType.EXECUTOR);
    }

    @Override
    protected void initService() throws ServiceInitException {
        int size = Runtime.getRuntime().availableProcessors() << 1;
        executorServices = new ExecutorService[size];
        ThreadFactory tf = new ServerThreadFactory("Gate-Server-Work");
        for (int i = 0; i < size; ++i) {
            executorServices[i] = Executors.newSingleThreadExecutor(tf);
        }
        if (ExecutorChooser.isPowerOfTwo(size)) executorChooser = new PowerOfTwoExecutorChooser(executorServices);
        else executorChooser = new GenericExecutorChooser(executorServices);
        scheduleExecutor = Executors.newScheduledThreadPool(size >> 1, new ServerThreadFactory("Gate-Server-Schedule"));
    }

    @Override
    protected void startService() throws ServiceStartException {
    }

    @Override
    protected void stopService() throws ServiceStopException {
        if (executorServices != null) {
            for (ExecutorService es : executorServices) {
                try {
                    es.shutdown();
                    es.awaitTermination(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public ExecutorService getExecutor() {
        return executorChooser.next();
    }

    @Override
    public ExecutorService getExecutor(long id) {
        return executorChooser.next(id);
    }

    @Override
    public ScheduledExecutorService getScheduleExecutor() {
        return this.scheduleExecutor;
    }

    public static class ExecutorChooser {
        protected ExecutorService[] executors;
        protected AtomicInteger executorIdx;

        private static boolean isPowerOfTwo(int val) {
            return (val & -val) == val;
        }

        static final int hash(long id) {
            int h = (int) (id ^ id >>> 32);
            return (h ^ (h >>> 16)) & 0x7fffffff;
        }

        public interface IExecutorChooser {
            ExecutorService next();

            ExecutorService next(long id);
        }

        public static final class PowerOfTwoExecutorChooser extends ExecutorChooser implements IExecutorChooser {
            public PowerOfTwoExecutorChooser(ExecutorService[] executors) {
                this.executors = executors;
                this.executorIdx = new AtomicInteger(0);
            }

            @Override
            public ExecutorService next() {
                return executors[executorIdx.getAndIncrement() & executors.length - 1];
            }

            @Override
            public ExecutorService next(long id) {
                return executors[hash(id) & (executors.length - 1)];
            }
        }

        public static final class GenericExecutorChooser extends ExecutorChooser implements IExecutorChooser {
            public GenericExecutorChooser(ExecutorService[] executors) {
                this.executors = executors;
                this.executorIdx = new AtomicInteger(0);
            }

            @Override
            public ExecutorService next() {
                return executors[Math.abs(executorIdx.getAndIncrement() % executors.length)];
            }

            @Override
            public ExecutorService next(long id) {
                return executors[hash(id) % executors.length];
            }
        }
    }
}