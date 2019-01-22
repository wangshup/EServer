package com.dd.server.event;

import com.dd.server.Server;
import com.dd.server.entities.IUser;
import com.dd.server.entities.IZone;
import com.dd.server.event.param.IServerEvent;
import com.dd.server.event.param.ServerEvent;
import com.dd.server.event.param.ServerEventParam;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.exceptions.EventHandleException;
import com.dd.server.extensions.IExtension;
import com.dd.server.request.Request;

import java.util.HashMap;
import java.util.Map;

public class DisconnectionSystemHandler extends AbstractSystemRequestHandler {
    public DisconnectionSystemHandler() {
        super(ServerEventType.DISCONNECT);
    }

    public boolean validate(Request request) {
        return true;
    }

    public void execute(Request request) {
        IUser user = Server.getInstance().getExtensionService().getUserBySession(request.getSession());
        if (user == null) {
            throw new IllegalArgumentException("Logout failure. Session is not logged in: " + request.getSession());
        }

        IZone zone = user.getZone();
        if (zone != null) {
            final IExtension extension = zone.getRunningExtension();
            Map<ServerEventParam, Object> evtParams = new HashMap<>();
            evtParams.put(ServerEventParam.USER, user);
            IServerEvent event = new ServerEvent(ServerEventType.DISCONNECT, evtParams);
            try {
                extension.handleServerEvent(event);
            } catch (EventHandleException e) {
                logger.error("user {} disconnect error", e);
            } finally {
                zone.removeUser(user);
            }
        }
    }
}