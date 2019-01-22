package com.dd.server.services;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.server.Server;
import com.dd.server.session.ISession;

import io.netty.channel.Channel;

public class ServerSessionService implements ISessionService {
	private static final Logger logger = LoggerFactory.getLogger(ServerSessionService.class);
	private Map<Integer, ISession> sessionMap = new ConcurrentHashMap<Integer, ISession>();
	private Map<Channel, ISession> sessionChannelMap = new ConcurrentHashMap<Channel, ISession>();
	private AtomicLong totalSessions = new AtomicLong(0L);

	@Override
	public void addSession(ISession session) {
		addSession(session, true);
	}

	@Override
	public void addSession(ISession session, boolean increseCountFlag) {
		if (increseCountFlag) {
			this.totalSessions.incrementAndGet();
		}
		this.sessionMap.put(Integer.valueOf(session.getSessionId()), session);
		Channel channel = session.getChannel();
		if (channel != null)
			this.sessionChannelMap.put(channel, session);
	}

	@Override
	public int getCurrentSessionsCount() {
		return this.sessionChannelMap.size();
	}

	@Override
	public long getTotalSessionsCount() {
		return this.totalSessions.get();
	}

	@Override
	public ISession removeSession(Channel channel) {
		ISession session = sessionChannelMap.get(channel);
		removeSession(session);
		return session;
	}

	@Override
	public void removeSession(ISession session) {
		if (session != null) {
			sessionMap.remove(session.getSessionId());
			Channel channel = session.getChannel();
			if (channel != null) {
				sessionChannelMap.remove(channel);
			}
		}
	}

	@Override
	public ISession removeSession(int sessionId) {
		ISession session = sessionMap.get(sessionId);
		if (session != null) {
			removeSession(session);
		}
		return session;
	}

	@Override
	public ISession getSession(Channel channel) {
		return sessionChannelMap.get(channel);
	}

	@Override
	public ISession getSession(int sessionId) {
		return sessionMap.get(sessionId);
	}

	@Override
	public boolean containsSession(ISession session) {
		return sessionMap.containsKey(session.getSessionId());
	}

	@Override
	public ISession getSession(Channel channel, int sessionId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkSessionTimeout() {
		try {
			for (Iterator<Entry<Channel, ISession>> it = sessionChannelMap.entrySet().iterator(); it.hasNext();) {
				Entry<Channel, ISession> e = it.next();
				ISession session = e.getValue();
				Channel channel = e.getKey();
				if (session.isTimeout()) {
					it.remove();
					sessionMap.remove(session.getSessionId());
					if (channel.isActive())
						channel.close();
					logger.info("close timeout session: {}", session);
				}
			}
		} catch (Exception e) {
			logger.error("session time out check error!", e);
		}
	}

	@Override
	public void checkUserTimeout() {
		Server.getInstance().getExtensionService().checkTimeoutUsers(true);
	}
}