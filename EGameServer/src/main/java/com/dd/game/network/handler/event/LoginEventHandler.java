package com.dd.game.network.handler.event;

import com.dd.edata.db.DBWhere;
import com.dd.game.core.GameEngine;
import com.dd.game.entity.model.PlayerModel;
import com.dd.game.entity.player.Player;
import com.dd.game.network.handler.AbstractEventHandler;
import com.dd.game.utils.FunctionSwitch;
import com.dd.protobuf.LoginProtocol;
import com.dd.protobuf.LoginProtocol.SCLogin;
import com.dd.protobuf.PBStructProtocol.PBPlayerInfo;
import com.dd.server.Server;
import com.dd.server.annotation.MsgHandler;
import com.dd.server.entities.IUser;
import com.dd.server.entities.IZone;
import com.dd.server.event.param.IServerEvent;
import com.dd.server.event.param.ServerEventParam;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.exceptions.ServerErrorCode;
import com.dd.server.exceptions.ServerErrorData;
import com.dd.server.exceptions.ServerLoginException;
import com.dd.server.session.ISession;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登陆处理
 */
@MsgHandler(id = ServerEventType.LOGIN, name = "登录")
public class LoginEventHandler extends AbstractEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginEventHandler.class);

    @Override
    protected Message handleEvent(IServerEvent event) throws Exception {
        long startTime = System.currentTimeMillis();
        IZone zone = event.getParameter(ServerEventParam.ZONE);
        ISession sender = event.getParameter(ServerEventParam.SESSION);
        LoginProtocol.CSLogin login = event.getParameter(ServerEventParam.LOGIN_IN_DATA);
        String strLoginInfo = jsonFormat.printToString(login);
        if (!Server.getInstance().getSessionService().containsSession(sender)) {
            throw new ServerLoginException(new StringBuilder().append("Login failed: user: ").append(sender.toString()).append(" , session is already expired!").toString(), new ServerErrorData(ServerErrorCode.GENERIC_ERROR));
        }

        try {
            PlayerModel playerModel = GameEngine.getEData().select(PlayerModel.class, DBWhere.equal("deviceId", login.getDeviceId()));
            Player player;
            SCLogin.Builder builder = SCLogin.newBuilder();
            String strAppVer = login.getAppVer() != null ? login.getAppVer() : "1.0.0";
            if (playerModel != null) {
                player = Player.login(playerModel.getId(), sender.getAddress(), login.getDeviceId(), login.getGaid(), login.getCountry(), login.getPlatform(), strAppVer);
            } else {
                player = Player.preRegister(login.getAccount(), sender.getAddress(), login.getDeviceId(), login.getGaid(), login.getCountry(), login.getPlatform(), strAppVer);
                player.register();
                builder.setIsRegister(true);
            }
            IUser user = zone.getUserBySession(sender);
            player.setProtoVer(login.getProtoVer());
            player.setUser(user);
            builder.setPlayerInfo((PBPlayerInfo) player.toProtoBuf());
            builder.setServerTime(String.valueOf(System.currentTimeMillis()));
            builder.setFunctions(FunctionSwitch.buildFunctionStr(player, player.getLevel()));
            builder.setUdpIp(zone.getUdpIp());
            builder.setUdpPort(zone.getUpdPort());
            builder.setSessionId(sender.getSessionId());
            Message msg = builder.build();
            logger.info("|{}|user {} login success | login info:{} | player info:{}", System.currentTimeMillis() - startTime, user, strLoginInfo, jsonFormat.printToString(msg));
            return msg;
        } catch (ServerLoginException le) {
            logger.error("user login or register error, login info:{}", strLoginInfo, le);
            throw le;
        } catch (Exception e) {
            logger.error("user login or register error, login info:{}", strLoginInfo, e);
            throw new ServerLoginException(new StringBuilder().append("Login or register failed: user: ").append(sender.toString()).toString(), new ServerErrorData(ServerErrorCode.GENERIC_ERROR));
        }
    }
}
