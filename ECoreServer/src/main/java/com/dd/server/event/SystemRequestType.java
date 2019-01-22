package com.dd.server.event;

import com.dd.server.event.param.ServerEventType;

public enum SystemRequestType {

    Handshake(ServerEventType.HANDSHAKE),

    Login(ServerEventType.LOGIN),

    Logout(ServerEventType.LOGOUT),

    PingPong(ServerEventType.PINGPONG),

    Disconnection(ServerEventType.DISCONNECT),
    ;

    private String id;

    SystemRequestType(String id) {
        this.id = id;
    }

    public static SystemRequestType fromId(String id) {
        for (SystemRequestType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("system request type " + id + " is not exists");
    }

    public String getId() {
        return id;
    }
}