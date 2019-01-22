package com.dd.server.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSystemRequestHandler implements ISystemRequestHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected String eventType;

    public AbstractSystemRequestHandler(String eventType) {
        this.eventType = eventType;
    }
}