package com.dd.server.event.param;

public interface ServerEventType {
    // 系统事件
    String SYSTEM_EVENT = "system";

    String HANDSHAKE = SYSTEM_EVENT + ".handshake";
    String LOGIN = SYSTEM_EVENT + ".login";
    String JOIN_ZONE = SYSTEM_EVENT + ".zone.join";
    String LOGOUT = SYSTEM_EVENT + ".logout";
    String DISCONNECT = SYSTEM_EVENT + ".disconnect";
    String PINGPONG = SYSTEM_EVENT + ".pingpong";
    String GATE_REGISTER = SYSTEM_EVENT + ".register";
    String GATE_PINGPONG = SYSTEM_EVENT + ".gate.pingpong";
}