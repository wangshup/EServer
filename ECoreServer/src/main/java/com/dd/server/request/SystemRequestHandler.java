package com.dd.server.request;

import com.dd.server.event.*;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class SystemRequestHandler {
    private static Map<SystemRequestType, ISystemRequestHandler> handlerMap;

    static {
        handlerMap = ImmutableMap.of(
                // 底层握手包，在建立连接后的第一个通信协议
                SystemRequestType.Handshake, new HandshakeSystemHandler(),

                // 登录
                SystemRequestType.Login, new LoginSystemHandler(),

                // 登出
                SystemRequestType.Logout, new LogoutSystemHandler(),

                // 手动断线
                SystemRequestType.Disconnection, new DisconnectionSystemHandler(),

                // 心跳包
                SystemRequestType.PingPong, new PingPongSystemHandler());
    }

    public static void handleRequest(Request request) throws Exception {
        SystemRequestType type = SystemRequestType.fromId(request.getId());
        ISystemRequestHandler handler = handlerMap.get(type);
        if (handler == null) {
            throw new NullPointerException("system request type " + type.toString() + " does not supported");
        }
        if (handler.validate(request)) {
            handler.execute(request);
        }
    }
}