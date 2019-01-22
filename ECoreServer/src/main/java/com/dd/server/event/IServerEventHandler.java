package com.dd.server.event;

import com.dd.server.event.param.IServerEvent;
import com.dd.server.exceptions.ServerHandleException;
import com.google.protobuf.Message;

public interface IServerEventHandler {
    Message handleServerEvent(IServerEvent event) throws ServerHandleException;
}