package com.dd.gate.services;

import com.dd.gate.entities.InnerServer;
import com.dd.gate.session.ISession;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SessionService extends AbstractService {
    private Map<Integer, ISession> sessions = new ConcurrentHashMap<>();
    private Map<Channel, ISession> channelSessions = new ConcurrentHashMap<>();
    private Map<Integer, InnerServer> innerServers = new ConcurrentHashMap<>();
    private Map<Channel, InnerServer> channelInnerServers = new ConcurrentHashMap<>();
    private AtomicLong totalSessions = new AtomicLong(0L);

    public SessionService() {
        super(ServiceType.SESSION);
    }

    public void addSession(ISession session) {
        addSession(session, true);
    }

    public void addSession(ISession session, boolean increseCountFlag) {
        if (increseCountFlag) {
            this.totalSessions.incrementAndGet();
        }
        sessions.put(session.getSessionId(), session);
        channelSessions.put(session.getChannel(), session);
    }

    public int getCurrentSessionsCount() {
        return this.channelSessions.size();
    }

    public long getTotalSessionsCount() {
        return this.totalSessions.get();
    }

    public ISession removeSession(Channel channel) {
        ISession session = channelSessions.get(channel);
        removeSession(session);
        return session;
    }

    public void removeSession(ISession session) {
        if (session != null) {
            sessions.remove(session.getSessionId());
            channelSessions.remove(session.getChannel());
            InnerServer s = channelInnerServers.remove(session.getChannel());
            if (s != null) innerServers.remove(s.getServerId());
        }
    }

    public ISession removeSession(int sessionId) {
        ISession session = sessions.get(sessionId);
        if (session != null) {
            removeSession(session);
        }
        return session;
    }

    public ISession getSession(Channel channel) {
        return channelSessions.get(channel);
    }

    public ISession getSession(int sessionId) {
        return sessions.get(sessionId);
    }

    public boolean containsSession(ISession session) {
        return sessions.containsKey(session.getSessionId());
    }

    public void removeInnerServer(Channel channel) {
        InnerServer s = channelInnerServers.remove(channel);
        if (s != null) {
            innerServers.remove(s.getServerId());
        }
    }

    public void addInnerServer(InnerServer server) {
        innerServers.put(server.getServerId(), server);
        channelInnerServers.put(server.getSession().getChannel(), server);
    }

    public InnerServer getInnerServer(int serverId) {
        return innerServers.get(serverId);
    }

    public InnerServer getInnerServer(String deviceId) {
        if (innerServers.isEmpty()) return null;
        Integer[] keys = innerServers.keySet().toArray(new Integer[0]);
        return getInnerServer(keys[Math.abs(deviceId.hashCode()) % keys.length]);
    }
}