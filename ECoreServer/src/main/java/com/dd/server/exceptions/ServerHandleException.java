package com.dd.server.exceptions;

@SuppressWarnings("serial")
public class ServerHandleException extends EventHandleException {
    ServerErrorData errorData;

    public ServerHandleException(String message) {
        super(message);
        this.errorData = null;
    }

    public ServerHandleException(String message, ServerErrorData data) {
        super(message);
        this.errorData = data;
    }

    public ServerHandleException(Throwable t) {
        super(t);
        this.errorData = null;
    }

    public ServerErrorData getErrorData() {
        return this.errorData;
    }
}
