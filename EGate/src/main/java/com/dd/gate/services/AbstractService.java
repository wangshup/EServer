package com.dd.gate.services;

import com.dd.gate.exceptions.ServiceInitException;
import com.dd.gate.exceptions.ServiceStartException;
import com.dd.gate.exceptions.ServiceStopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractService implements IService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected ServiceType serviceType;
    private boolean initialized;
    private volatile boolean started;

    public AbstractService(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public void init() throws ServiceInitException {
        if (this.initialized) {
            throw new ServiceInitException(getName() + " already initialized");
        }
        initService();
        this.logger.info("[gate] {} initialized", getName());
        this.initialized = true;
    }

    public void start() throws ServiceStartException {
        if (!this.initialized) {
            throw new ServiceStartException(getName() + " has not initialized");
        }
        if (this.started) {
            throw new ServiceStartException(getName() + " already started");
        }
        startService();
        this.logger.info("[gate] {} started", getName());
        this.started = true;
    }

    public void stop() throws ServiceStopException {
        if (!this.started) {
            throw new ServiceStopException(getName() + " has not started");
        }
        stopService();
        this.logger.info("[gate] {} stopped", getName());
        this.started = false;
    }

    public void restart() throws ServiceStartException, ServiceStopException {
        stop();
        start();
    }

    public String getName() {
        return "core service " + this.serviceType.name();
    }

    public ServiceType getServiceType() {
        return this.serviceType;
    }

    protected void initService() throws ServiceInitException {
    }

    protected void startService() throws ServiceStartException {
    }

    protected void stopService() throws ServiceStopException {
    }
}