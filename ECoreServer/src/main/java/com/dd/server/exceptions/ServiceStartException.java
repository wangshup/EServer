package com.dd.server.exceptions;

@SuppressWarnings("serial")
public class ServiceStartException extends Exception {
    public ServiceStartException(String message) {
        super(message);
    }

    public ServiceStartException(String message, Throwable cause) {
        super(message, cause);
    }
}