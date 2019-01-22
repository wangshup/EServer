package com.dd.gate.io;

import com.dd.gate.GateServer;
import com.dd.gate.services.SessionService;
import com.dd.gate.session.ISession;
import com.dd.gate.session.InnerSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InnerChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(InnerChannelHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ISession session = new InnerSession(ctx.channel());
        GateServer.getInstance().getSessionService().addSession(session);
        logger.info("[gate] inner session created, id {}, ip {}, type: SOCKET", session.getSessionId(), session.getAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionService ss = GateServer.getInstance().getSessionService();
        ss.removeInnerServer(ctx.channel());
        ISession session = ss.removeSession(ctx.channel());
        logger.info("[gate] inner session destroyed, id {}, ip {}, reads:{}, sends:{}, maxRead:{}, maxSend:{} type: SOCKET", session.getSessionId(), session.getAddress(), session.getReadBytes(), session.getSendBytes(), session.getMaxRead(), session.getMaxSend());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ISession session = GateServer.getInstance().getSessionService().getSession(ctx.channel());
        GateServer.getInstance().getInnerHandlerService().handle(session, (ByteBuf) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            ISession session = GateServer.getInstance().getSessionService().getSession(ctx.channel());
            logger.error("[gate] inner channel handler error, session: {}", session, cause);
        } finally {
            ctx.close();
        }
    }
}