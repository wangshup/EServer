package com.dd.game.network.handler;

import com.dd.game.entity.player.Player;
import com.dd.game.entity.player.PlayerManager;
import com.dd.game.exceptions.GameException;
import com.dd.game.exceptions.GameExceptionCode;
import com.dd.game.utils.Constants;
import com.dd.server.entities.IUser;
import com.dd.server.request.IClientRequestHandler;
import com.dd.server.request.Request;
import com.dd.server.request.Response;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public abstract class AbstractRequestHandler implements IClientRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRequestHandler.class);
    private static final JsonFormat jsonFormat = new JsonFormat();

    /**
     * 异常处理
     *
     * @param msgId
     * @param ge
     * @param playerId
     * @param zoneId
     */
    private static void responseException(String msgId, GameException ge, long playerId, int zoneId) {
        if (zoneId == Constants.SERVER_ID) {
            Player player = PlayerManager.getInstance().getPlayer(playerId);
            if (player != null && player.getUser() != null)
                Response.sendErrorResponse(msgId, ge.getExceptionCode().getCode(), ge.getExceptionInfo(), player.getUser());
        } else {
            // 把该msg传到目标服上
        }
    }

    public static void responseMessage(String msgId, Message msg, long playerId, int zoneId) {
        if (zoneId == Constants.SERVER_ID) {
            Player player = PlayerManager.getInstance().getPlayer(playerId);
            if (player != null && player.getUser() != null) Response.send(msgId, msg, player.getUser());
        } else {
            // 把该msg传到目标服上
        }
    }

    public static void responseMessageWithLog(String msgId, Message msg, long playerId, int zoneId, String logCmd) {
        responseMessage(msgId, msg, playerId, zoneId);
        logRequestMsg(logCmd, msgId, null, msg, playerId, zoneId, System.currentTimeMillis());
    }

    public static void exceptionHandler(Throwable e, String msgId, long playerId, int zoneId, Message req) {
        if (e instanceof GameException) {
            responseException(msgId, (GameException) e, playerId, zoneId);
        } else if (e instanceof ExecutionException) {
            if (e.getCause() instanceof GameException) {
                responseException(msgId, (GameException) e.getCause(), playerId, zoneId);
            } else {
                responseException(msgId, new GameException(GameExceptionCode.INVALID_OPT), playerId, zoneId);
            }
        } else {
            responseException(msgId, new GameException(GameExceptionCode.INVALID_OPT), playerId, zoneId);
        }
        String strErr = String.format("%s request %s from server %s, msg body: %s", playerId, msgId, zoneId, req == null ? "ERROR" : jsonFormat.printToString(req));
        logger.error(strErr, e);
    }

    public static void logRequestMsg(String cmd, String msgId, Message req, Message resp, long playerId, int zoneId, long startTime) {
        try {
            StringBuffer logMsg = new StringBuffer();
            logMsg.append(zoneId).append(Constants.LOG_SEPARATOR).append(cmd).append(Constants.LOG_SEPARATOR).append(msgId).append(Constants.LOG_SEPARATOR).append(playerId).append(Constants.LOG_SEPARATOR).append(System.currentTimeMillis() - startTime).append(Constants.LOG_SEPARATOR).append(req == null ? "ERROR REQ" : jsonFormat.printToString(req)).append(Constants.LOG_SEPARATOR).append(resp == null ? "ERROR RESP" : jsonFormat.printToString(resp));
            logger.info(logMsg.toString());
        } catch (Exception e) {
            logger.error("record player action", e);
        }
    }

    @Override
    public void handleClientRequest(IUser user, Request msg) {
        long startTime = System.currentTimeMillis();
        Message req = msg.getBody();
        Message resp = null;
        Player player = null;

        msg.setHandleStartTime(startTime);
        try {
            player = PlayerManager.getInstance().getPlayer(user);
            resp = handle(player, msg);
            if (resp != null) {
                Response.send(msg.getId(), resp, user);
            }
        } catch (Throwable e) {
            exceptionHandler(e, msg.getId(), player != null ? player.getId() : 0, Constants.SERVER_ID, req);
        } finally {
            logRequestMsg("CMD", msg.getId(), req, resp, player != null ? player.getId() : 0, Constants.SERVER_ID, startTime);
        }
    }

    /**
     * 消息具体处理类
     *
     * @param player 玩家对象
     * @param msg    请求消息体
     * @return 返回消息体（如果返回消息为null，则该消息不返回给前端）
     * @throws Exception 异常处理
     */
    public abstract Message handle(Player player, Request msg) throws Exception;
}
