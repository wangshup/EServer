package com.dd.server.exceptions;

@SuppressWarnings("serial")
public class SystemRequestValidationException extends Exception {
    public SystemRequestValidationException() {
    }

    public SystemRequestValidationException(String message) {
        super(message);
    }

    public SystemRequestValidationException(Throwable t) {
        super(t);
    }
}