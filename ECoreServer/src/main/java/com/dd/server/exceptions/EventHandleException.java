package com.dd.server.exceptions;

@SuppressWarnings("serial")
public class EventHandleException extends RequestException {
    public EventHandleException(String message, ServerErrorData data) {
        super(message, data);
    }

    public EventHandleException(String message) {
        super(message);
    }

    public EventHandleException(Throwable cause) {
        super(cause);
    }

    public EventHandleException(String message, Throwable cause) {
        super(message, cause);
    }
}