package com.dd.server.exceptions;

@SuppressWarnings("serial")
public class ServerLoginException extends ServerHandleException {

    public ServerLoginException(String message) {
        super(message);
    }

    public ServerLoginException(String message, ServerErrorData data) {
        super(message, data);
    }
}