package com.dd.server.exceptions;

@SuppressWarnings("serial")
public class LoginException extends EventHandleException {
    public LoginException(String message, ServerErrorData data) {
        super(message, data);
    }

    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}