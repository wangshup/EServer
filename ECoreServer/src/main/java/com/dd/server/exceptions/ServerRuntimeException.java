package com.dd.server.exceptions;

@SuppressWarnings("serial")
public class ServerRuntimeException extends RuntimeException {
    public ServerRuntimeException() {
    }

    public ServerRuntimeException(String message) {
        super(message);
    }

    public ServerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerRuntimeException(Throwable cause) {
        super(cause);
    }
}