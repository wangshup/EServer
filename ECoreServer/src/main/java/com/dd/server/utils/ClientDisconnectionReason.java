package com.dd.server.utils;

public enum ClientDisconnectionReason {

    IDLE(0),

    KICK(1),

    BAN(2),

    KICK_QUIET(3),

    UNKNOWN(4),

    ;

    private final int value;

    ClientDisconnectionReason(int id) {
        this.value = id;
    }

    public int getValue() {
        return this.value;
    }
}