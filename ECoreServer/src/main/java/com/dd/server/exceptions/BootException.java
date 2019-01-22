package com.dd.server.exceptions;

public class BootException extends Exception {
    private static final long serialVersionUID = 1L;

    public BootException() {
    }

    public BootException(String message) {
        super(message);
    }
}