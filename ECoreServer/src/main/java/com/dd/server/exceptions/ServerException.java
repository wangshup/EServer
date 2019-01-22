package com.dd.server.exceptions;

@SuppressWarnings("serial")
public class ServerException extends Exception {
    private ServerErrorData errorData;

    public ServerException() {
    }

    public ServerException(String message, ServerErrorData data) {
        super(message);
        this.errorData = data;
    }

    public ServerErrorData getErrorData() {
        return this.errorData;
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }
}