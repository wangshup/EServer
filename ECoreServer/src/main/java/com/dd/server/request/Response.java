package com.dd.server.request;

import com.dd.protobuf.CommonProtocol.PacketHead;
import com.dd.server.entities.IUser;
import com.dd.server.event.SystemRequestType;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.exceptions.EventHandleException;
import com.dd.server.session.ISession;
import com.dd.server.utils.ClientDisconnectionReason;
import com.dd.server.utils.Constants;
import io.netty.channel.ChannelFuture;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Response extends Message {
    private static final Logger logger = LoggerFactory.getLogger(Response.class);
    private int gateSessionId;

    public Response(PacketHead head) {
        if (head == null) {
            throw new IllegalArgumentException("PacketHead not valied! ");
        }
        this.head = head;
    }

    public Response(String id) {
        PacketHead.Builder hBuilder = PacketHead.newBuilder();
        hBuilder.setActionId(id);
        this.head = hBuilder.build();
    }

    public Response(String id, com.google.protobuf.Message msg) {
        super(0, id, msg);
    }

    public static void send(String cmdName, com.google.protobuf.Message msg, List<IUser> recipients) {
        for (IUser u : recipients)
            send(cmdName, msg, u);
    }

    public static void sendLoginResponse(ISession user, com.google.protobuf.Message msg) {
        if (user == null) {
            logger.error("session is null, can't send response,msg:{}", msg);
            return;
        }

        user.writeResponse(new Response(ServerEventType.LOGIN, msg));
    }

    public static void sendLoginErrorResponse(ISession user, EventHandleException err) {
        if (user == null) {
            logger.error("session is null, can't send response,err:{}", err);
            return;
        }

        PacketHead.Builder hBuilder = PacketHead.newBuilder();
        hBuilder.setActionId(ServerEventType.LOGIN);
        hBuilder.setErrorCode(err.getErrorData().getCode().getId());
        user.writeResponse(new Response(hBuilder.build()));
    }

    public static ChannelFuture sendDisconnectResponse(ClientDisconnectionReason reason, IUser recipient) {
        PacketHead.Builder hBuilder = PacketHead.newBuilder();
        hBuilder.setActionId(ServerEventType.DISCONNECT);
        hBuilder.setErrorCode(reason.getValue());
        return recipient.getSession().writeResponse(new Response(hBuilder.build()));
    }

    public static void send(String cmdName, com.google.protobuf.Message params, ISession session) {
        Response msg = new Response(cmdName, params);
        session.writeResponse(msg);
    }

    public static void send(String cmdName, com.google.protobuf.Message params, IUser recipient) {
        send(cmdName, params, recipient.getSession());
    }

    public static void sendEvent(String eventType, com.google.protobuf.Message params, ISession recipient) {
        send(eventType, params, recipient);
    }

    public static void sendEvent(SystemRequestType requestType, com.google.protobuf.Message params, ISession recipient) {
        sendEvent(requestType.getId(), params, recipient);
    }

    public static void sendErrorResponse(String cmdName, int errCode, String errInfo, IUser user) {
        if (user == null) {
            logger.error("session is null, can't send response,err:{}", errCode);
            return;
        }

        PacketHead.Builder hBuilder = PacketHead.newBuilder();
        hBuilder.setActionId(cmdName);
        hBuilder.setErrorCode(errCode);
        if (StringUtils.isNotBlank(errInfo)) {
            hBuilder.setErrorInfo(errInfo);
        }
        user.getSession().writeResponse(new Response(hBuilder.build()));
    }

    public int getGateSessionId() {
        return gateSessionId;
    }

    public void setGateSessionId(int gateSessionId) {
        this.gateSessionId = gateSessionId;
    }

    public byte getInnerCmd() {
        switch (head.getActionId()) {
            case ServerEventType.GATE_REGISTER:
                return Constants.CMD_INNER_REGISTER;
            case ServerEventType.GATE_PINGPONG:
                return Constants.CMD_INNER_PINGPONG;
            case ServerEventType.DISCONNECT:
                return Constants.CMD_INNER_DISCONNECT;
            default:
                return Constants.CMD_INNER_TUNNEL;
        }
    }
}
