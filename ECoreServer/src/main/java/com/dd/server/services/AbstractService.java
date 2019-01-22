package com.dd.server.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.server.exceptions.ServiceInitException;
import com.dd.server.exceptions.ServiceStartException;
import com.dd.server.exceptions.ServiceStopException;

public abstract class AbstractService implements IService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private boolean initialized;
    private volatile boolean started;
    protected ServiceType serviceType;

    public AbstractService(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public void init() throws ServiceInitException {
        if (this.initialized) {
            throw new ServiceInitException(getName() + " already initialized");
        }
        initService();
        this.logger.info("{} initialized", getName());
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
        this.logger.info("{} started", getName());
        this.started = true;
    }

    public void stop() throws ServiceStopException {
        if (!this.started) {
            throw new ServiceStopException(getName() + " has not started");
        }
        stopService();
        this.logger.info("{} stopped", getName());
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

    protected abstract void initService() throws ServiceInitException;

    protected abstract void startService() throws ServiceStartException;

    protected abstract void stopService() throws ServiceStopException;

    protected Properties getConfigProperties(String configFile) {
        Properties configProperties = new Properties();
        try (InputStream ins = new FileInputStream(configFile)) {
            configProperties.load(ins);
        } catch (IOException e) {
            logger.error("read config {} properties error", configFile, e);
        }
        return configProperties;
    }
}