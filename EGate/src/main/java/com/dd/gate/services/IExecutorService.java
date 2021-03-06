package com.dd.gate.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface IExecutorService {

    ExecutorService getExecutor();

    ExecutorService getExecutor(long id);

    ScheduledExecutorService getScheduleExecutor();
}