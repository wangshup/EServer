package com.dd.server.mq;

public class MqException extends Exception {

    private int error;

    public MqException() {
        super();
    }

    public MqException(int error) {
        super(String.valueOf(error));
        this.error = error;
    }

    public MqException(int error, String info) {
        super(info);
        this.error = error;
    }

    public int getError() {
        return error;
    }
}
