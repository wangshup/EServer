package com.dd.server.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.server.Server;
import com.dd.server.entities.IUser;
import com.dd.server.entities.IZone;
import com.dd.server.event.param.IServerEvent;
import com.dd.server.event.param.ServerEvent;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.exceptions.EventHandleException;
import com.dd.server.extensions.IExtension;
import com.dd.server.request.Request;

public class LogoutSystemHandler extends AbstractSystemRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(LogoutSystemHandler.class);

    public LogoutSystemHandler() {
        super(ServerEventType.LOGOUT);
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
            zone.removeUser(user);
            final IExtension extension = zone.getRunningExtension();
            IServerEvent event = new ServerEvent(ServerEventType.LOGOUT);
            try {
                extension.handleServerEvent(event);
            } catch (EventHandleException e) {
                logger.error("user {} logout error", e);
            }
        }
    }
}