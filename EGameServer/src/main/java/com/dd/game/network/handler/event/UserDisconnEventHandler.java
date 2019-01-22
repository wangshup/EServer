package com.dd.game.network.handler.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.game.entity.player.Player;
import com.dd.game.entity.player.PlayerManager;
import com.dd.game.network.handler.AbstractEventHandler;
import com.dd.server.annotation.MsgHandler;
import com.dd.server.entities.IUser;
import com.dd.server.event.param.IServerEvent;
import com.dd.server.event.param.ServerEventParam;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.utils.ClientDisconnectionReason;
import com.google.protobuf.Message;

@MsgHandler(id = ServerEventType.DISCONNECT, name = "断线处理")
public class UserDisconnEventHandler extends AbstractEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserDisconnEventHandler.class);

    @Override
    protected Message handleEvent(IServerEvent event) throws Exception {
        IUser user = event.getParameter(ServerEventParam.USER);
        if (user != null && user.getProperty("uid") != null) {
            Long uid = (Long) user.getProperty("uid");
            Player player = PlayerManager.getInstance().getPlayer(uid);
            ClientDisconnectionReason reason = event.getParameter(ServerEventParam.DISCONNECTION_REASON);
            if (player != null) {
                player.disconnect(reason);
            }
        }
        logger.info("user {} disconnect success", user);
        return null;
    }
}
