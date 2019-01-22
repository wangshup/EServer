package com.dd.gate.services;

import com.dd.gate.exceptions.ServiceInitException;
import com.dd.gate.exceptions.ServiceStartException;
import com.dd.gate.exceptions.ServiceStopException;

public interface IService {
    void init() throws ServiceInitException;

    void start() throws ServiceStartException;

    void stop() throws ServiceStopException;

    void restart() throws ServiceStartException, ServiceStopException;

    ServiceType getServiceType();

    String getName();
}