package com.dd.gate.exceptions;

public class ServiceStopException extends Exception {
    public ServiceStopException(String message) {
        super(message);
    }

    public ServiceStopException(String message, Throwable cause) {
        super(message, cause);
    }
}