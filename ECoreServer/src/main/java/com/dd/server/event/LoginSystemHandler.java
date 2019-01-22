package com.dd.server.event;

import com.dd.protobuf.LoginProtocol;
import com.dd.protobuf.LoginProtocol.CSLogin;
import com.dd.server.Server;
import com.dd.server.entities.GameUser;
import com.dd.server.entities.IUser;
import com.dd.server.entities.IZone;
import com.dd.server.event.param.IServerEvent;
import com.dd.server.event.param.ServerEvent;
import com.dd.server.event.param.ServerEventParam;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.exceptions.*;
import com.dd.server.extensions.IExtension;
import com.dd.server.request.Request;
import com.dd.server.request.Response;
import com.dd.server.session.ISession;
import com.dd.server.utils.ClientDisconnectionReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LoginSystemHandler extends AbstractSystemRequestHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public LoginSystemHandler() {
        super(ServerEventType.LOGIN);
    }

    public boolean validate(Request request) throws SystemRequestValidationException {
        return true;
    }

    public void execute(final Request request) throws Exception {
        CSLogin login = LoginProtocol.CSLogin.newBuilder().mergeFrom(request.getBody(), 0, request.getBodyLen()).build();
        ISession sender = request.getSession();
        String zoneName = login.getZoneName();
        try {
            IZone zone = Server.getInstance().getExtensionService().getZone(zoneName);
            if (zone == null) {
                logger.error("can't find zone whit login {} session {}", login.toString(), sender);
                throw new ServerLoginException("zone is null", new ServerErrorData(ServerErrorCode.LOGIN_BAD_ZONENAME));
            }
            IUser user = zone.addUser(new GameUser(sender, login.getDeviceId(), zone));
            if (user != null) {
                user.disconnect(login.getIsQuiet() ? ClientDisconnectionReason.KICK_QUIET : ClientDisconnectionReason.KICK);
            }

            final IExtension extension = zone.getRunningExtension();
            Map<ServerEventParam, Object> evtParams = new HashMap<>();
            evtParams.put(ServerEventParam.ZONE, zone);
            evtParams.put(ServerEventParam.SESSION, sender);
            evtParams.put(ServerEventParam.LOGIN_IN_DATA, login);
            IServerEvent event = new ServerEvent(ServerEventType.LOGIN, evtParams);
            Response.sendLoginResponse(sender, extension.handleServerEvent(event));
            // zone join
            extension.handleServerEvent(new ServerEvent(ServerEventType.JOIN_ZONE, evtParams));
        } catch (EventHandleException ex) {
            logger.error("login error, session info " + sender.toString(), ex);
            ServerErrorData errorData = ex.getErrorData();
            if (errorData != null) {
                Response.sendLoginErrorResponse(sender, ex);
            }
            throw ex;
        }
    }
}