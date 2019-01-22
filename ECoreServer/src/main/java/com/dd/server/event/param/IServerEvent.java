package com.dd.server.event.param;

public interface IServerEvent {
    String getType();

    <T> T getParameter(IServerEventParam paramISFSEventParam);
}