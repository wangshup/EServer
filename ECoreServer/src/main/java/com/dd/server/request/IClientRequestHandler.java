package com.dd.server.request;

import com.dd.server.entities.IUser;

public interface IClientRequestHandler {
    void handleClientRequest(IUser user, Request msg) throws Exception;
}