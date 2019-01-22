package com.dd.game.network.handler.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.game.entity.player.Player;
import com.dd.game.entity.player.PlayerManager;
import com.dd.game.network.handler.AbstractEventHandler;
import com.dd.server.annotation.MsgHandler;
import com.dd.server.entities.IUser;
import com.dd.server.entities.IZone;
import com.dd.server.event.param.IServerEvent;
import com.dd.server.event.param.ServerEventParam;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.utils.ClientDisconnectionReason;
import com.google.protobuf.Message;

@MsgHandler(id = ServerEventType.LOGOUT, name = "退出")
public class UserLogoutEventHandler extends AbstractEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserLogoutEventHandler.class);

    @Override
    protected Message handleEvent(IServerEvent event) throws Exception {
        IUser user = event.getParameter(ServerEventParam.USER);
        if (user != null) {
            Player player = PlayerManager.getInstance().getPlayer(user);
            if (player != null) {
                player.disconnect(ClientDisconnectionReason.UNKNOWN);
            }
            IZone zone = user.getZone();
            if (zone != null) {
                zone.removeUser(user);
            }
        }
        logger.info("user {} logout success", user);
        return null;
    }
}
