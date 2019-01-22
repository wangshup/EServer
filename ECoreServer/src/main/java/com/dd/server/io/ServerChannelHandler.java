package com.dd.server.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.server.Server;
import com.dd.server.entities.IUser;
import com.dd.server.request.Request;
import com.dd.server.session.ISession;
import com.dd.server.session.Session;
import com.dd.server.utils.ClientDisconnectionReason;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerChannelHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ServerChannelHandler.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ISession session = new Session(ctx.channel());
		Server.getInstance().getSessionService().addSession(session);
		logger.info("session created, id {}, ip {}, type: SOCKET", session.getSessionId(), session.getFullIpAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Server server = Server.getInstance();
		ISession session = server.getSessionService().removeSession(ctx.channel());
		if (session == null)
			return;

		IUser user = server.getExtensionService().getUserBySession(session);
		if (user != null)
			user.doDisconnect(ClientDisconnectionReason.UNKNOWN);
		logger.info("session destroyed, id {}, ip {}, reads:{}, sends:{}, maxRead:{}, maxSend:{} type: SOCKET",
				session.getSessionId(), session.getChannel().remoteAddress().toString(), session.getReadBytes(),
				session.getSendBytes(), session.getMaxRead(), session.getMaxSend());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!Request.class.isInstance(msg)) {
			return;
		}

		ISession session = Server.getInstance().getSessionService().getSession(ctx.channel());
		session.updateLastReadTime();
		((Request) msg).setSession(session);
		Server.getInstance().getRequestHandlerService().handleRequest((Request) msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		try {
			ISession session = Server.getInstance().getSessionService().removeSession(ctx.channel());
			String s = "unknown";
			String u = "unknown";
			if (session != null) {
				s = session.toString();
				IUser user = Server.getInstance().getExtensionService().getUserBySession(session);
				if (user != null) {
					u = user.toString();
				}
			}
			logger.error("channel handler error, session: {}, user: {}", s, u, cause);
		} finally {
			ctx.close();
		}
	}
}