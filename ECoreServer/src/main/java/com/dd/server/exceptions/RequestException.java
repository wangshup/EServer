package com.dd.server.exceptions;

@SuppressWarnings("serial")
public class RequestException extends Exception {
    private ServerErrorData errorData;

    public RequestException(String message, ServerErrorData data) {
        super(message);
        this.errorData = data;
    }

    public ServerErrorData getErrorData() {
        return this.errorData;
    }

    public RequestException(String message) {
        super(message);
    }

    public RequestException(Throwable cause) {
        super(cause);
    }

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }
}