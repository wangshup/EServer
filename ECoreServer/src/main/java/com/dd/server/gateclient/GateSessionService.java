package com.dd.server.gateclient;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.server.Server;
import com.dd.server.entities.IUser;
import com.dd.server.services.ISessionService;
import com.dd.server.session.ISession;
import com.dd.server.utils.ClientDisconnectionReason;

import io.netty.channel.Channel;

public class GateSessionService implements ISessionService {
	private static final Logger logger = LoggerFactory.getLogger(GateSessionService.class);
	private AtomicLong totalSessions = new AtomicLong(0L);
	public Map<Channel, Map<Integer, ISession>> sessions = new ConcurrentHashMap<>();

	@Override
	public void addSession(ISession session) {
		addSession(session, true);
	}

	@Override
	public void addSession(ISession session, boolean increseCountFlag) {
		Map<Integer, ISession> chSessions = sessions.get(session.getChannel());
		if (chSessions == null) {
			chSessions = new ConcurrentHashMap<>();
			sessions.put(session.getChannel(), chSessions);
		}
		chSessions.put(session.getSessionId(), session);
		if (increseCountFlag) {
			totalSessions.incrementAndGet();
		}
	}

	@Override
	public int getCurrentSessionsCount() {
		int count = 0;
		for (Map<Integer, ISession> map : sessions.values()) {
			count += map.size();
		}
		return count;
	}

	@Override
	public long getTotalSessionsCount() {
		return this.totalSessions.get();
	}

	@Override
	public ISession removeSession(Channel channel) {
		Map<Integer, ISession> map = sessions.remove(channel);
		if (map == null)
			return null;
		for (ISession s : map.values()) {
			IUser user = Server.getInstance().getExtensionService().getUserBySession(s);
			if (user != null) {
				user.doDisconnect(ClientDisconnectionReason.UNKNOWN);
			}
		}
		return null;
	}

	@Override
	public void removeSession(ISession session) {
		Map<Integer, ISession> chSessions = sessions.get(session.getChannel());
		if (chSessions == null)
			return;
		chSessions.remove(session.getSessionId());
		IUser user = Server.getInstance().getExtensionService().getUserBySession(session);
		if (user != null) {
			user.doDisconnect(ClientDisconnectionReason.UNKNOWN);
		}
	}

	@Override
	public ISession removeSession(int sessionId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISession getSession(Channel channel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISession getSession(int sessionId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsSession(ISession session) {
		Map<Integer, ISession> chSessions = sessions.get(session.getChannel());
		if (chSessions == null)
			return false;
		return chSessions.containsKey(session.getSessionId());
	}

	@Override
	public ISession getSession(Channel channel, int sessionId) {
		Map<Integer, ISession> chSessions = sessions.get(channel);
		if (chSessions == null)
			return null;
		return chSessions.get(sessionId);
	}

	@Override
	public void checkSessionTimeout() {
		try {
			for (Map<Integer, ISession> ss : sessions.values()) {
				for (Iterator<Entry<Integer, ISession>> it = ss.entrySet().iterator(); it.hasNext();) {
					Entry<Integer, ISession> e = it.next();
					ISession session = e.getValue();
					if (session.isTimeout()) {
						it.remove();
						logger.info("close timeout session: {}", session);
					}
				}
			}
		} catch (Exception e) {
			logger.error("session time out check error!", e);
		}
	}

	@Override
	public void checkUserTimeout() {
		Server.getInstance().getExtensionService().checkTimeoutUsers(false);
	}
}