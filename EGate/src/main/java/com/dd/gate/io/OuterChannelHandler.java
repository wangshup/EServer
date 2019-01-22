package com.dd.gate.io;

import com.dd.gate.GateServer;
import com.dd.gate.session.ISession;
import com.dd.gate.session.OuterSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OuterChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(OuterChannelHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ISession session = new OuterSession(ctx.channel());
        GateServer.getInstance().getSessionService().addSession(session);
        logger.info("[gate] outer session created, id {}, ip {}, type: SOCKET", session.getSessionId(), session.getAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        GateServer server = GateServer.getInstance();
        ISession session = server.getSessionService().removeSession(ctx.channel());
        if (session != null) {
            GateServer.getInstance().getOuterHandlerService().disconnect2InnerServer(session);
        }
        logger.info("[gate] outer session destroyed, ctx {}, type: SOCKET", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ISession session = GateServer.getInstance().getSessionService().getSession(ctx.channel());
        GateServer.getInstance().getOuterHandlerService().handle(session, (ByteBuf) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            ISession session = GateServer.getInstance().getSessionService().getSession(ctx.channel());
            logger.error("[gate] outer channel exception, session: {}", session);
        } finally {
            ctx.close();
        }
    }
}