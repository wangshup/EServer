package com.dd.game.network.handler.event;

import com.dd.game.entity.player.Player;
import com.dd.game.entity.player.PlayerManager;
import com.dd.game.module.event.EventDispatcher;
import com.dd.game.module.event.EventType;
import com.dd.game.network.handler.AbstractEventHandler;
import com.dd.server.annotation.MsgHandler;
import com.dd.server.entities.IUser;
import com.dd.server.entities.IZone;
import com.dd.server.event.param.IServerEvent;
import com.dd.server.event.param.ServerEventParam;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.session.ISession;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登陆成功之后的处理
 */
@MsgHandler(id = ServerEventType.JOIN_ZONE, name = "登录成功")
public class ZoneJoinEventHandler extends AbstractEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZoneJoinEventHandler.class);

    @Override
    protected Message handleEvent(IServerEvent event) throws Exception {
        IZone zone = event.getParameter(ServerEventParam.ZONE);
        ISession sender = event.getParameter(ServerEventParam.SESSION);
        IUser user = zone.getUserBySession(sender);
        Player player = PlayerManager.getInstance().getPlayer(user);
        EventDispatcher.fire(EventType.PLAYER_ZONE_JOIN, player);
        logger.info("user {} join success!", user);
        return null;
    }
}
