package com.dd.server.gateclient;

import com.dd.protobuf.GateProtocol;
import com.dd.protobuf.GateProtocol.CSInnerServerRegister;
import com.dd.server.Server;
import com.dd.server.entities.IZone;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.io.FrameWrapper;
import com.dd.server.request.Request;
import com.dd.server.request.Response;
import com.dd.server.services.ISessionService;
import com.dd.server.session.ISession;
import com.dd.server.session.Session;
import com.dd.server.utils.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class ClientChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ClientChannelHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Collection<IZone> zones = Server.getInstance().getExtensionService().getZones();
        for (IZone zone : zones) {
            CSInnerServerRegister csRegister = CSInnerServerRegister.newBuilder().setSid(zone.getZoneId()).setName(zone.getName()).setType(0).build();
            Response resp = new Response(ServerEventType.GATE_REGISTER, csRegister);
            ctx.writeAndFlush(resp);
        }
        logger.info("Register to gate successed, {}", ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Server.getInstance().getSessionService().removeSession(ctx.channel());
        logger.info("Disconnected from gate !!!, {}", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (!FrameWrapper.class.isInstance(msg)) {
                return;
            }

            FrameWrapper f = (FrameWrapper) msg;
            if (f.getInnerCmd() == Constants.CMD_INNER_REGISTER) {
                byte[] bytes = new byte[f.getDataFrame().readableBytes()];
                f.getDataFrame().readBytes(bytes);
                GateProtocol.SCInnerServerRegister scInner = GateProtocol.SCInnerServerRegister.parseFrom(bytes);
                IZone zone = Server.getInstance().getExtensionService().getZone(scInner.getSid());
                zone.setUdpIp(scInner.getUdpIp());
                zone.setUpdPort(scInner.getUdpPort());
            } else {
                ISessionService ss = Server.getInstance().getSessionService();
                ISession session = ss.getSession(ctx.channel(), f.getGateSessionId());
                if (session == null) {
                    session = new Session(ctx.channel(), f.getGateSessionId());
                    ss.addSession(session);
                }
                session.updateLastReadTime();
                Request req = new Request(f);
                req.setSession(session);
                Server.getInstance().getRequestHandlerService().handleRequest(req);
            }
        } catch (Exception e) {
            logger.error("channel read {} exception, channel closed", ctx, e);
            throw e;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            Server.getInstance().getSessionService().removeSession(ctx.channel());
            logger.error("channel {} exception, channel closed", ctx, cause);
        } finally {
            ctx.close();
        }
    }
}