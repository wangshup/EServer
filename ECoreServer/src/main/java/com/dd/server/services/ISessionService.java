package com.dd.server.services;

import com.dd.server.session.ISession;

import io.netty.channel.Channel;

public interface ISessionService {

	void addSession(ISession session);

	void addSession(ISession session, boolean increseCountFlag);

	int getCurrentSessionsCount();

	long getTotalSessionsCount();

	ISession removeSession(Channel channel);

	void removeSession(ISession session);

	ISession removeSession(int sessionId);

	ISession getSession(Channel channel);

	ISession getSession(int sessionId);

	ISession getSession(Channel channel, int sessionId);

	boolean containsSession(ISession session);

	void checkSessionTimeout();
	
	void checkUserTimeout();
}