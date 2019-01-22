package com.dd.server.services;

import com.dd.server.exceptions.ServiceInitException;
import com.dd.server.exceptions.ServiceStartException;
import com.dd.server.exceptions.ServiceStopException;

public interface IService {
    void init() throws ServiceInitException;

    void start() throws ServiceStartException;

    void stop() throws ServiceStopException;

    void restart() throws ServiceStartException, ServiceStopException;

    ServiceType getServiceType();

    String getName();
}