package com.dd.gate.entities;

import com.dd.gate.session.ISession;
import com.dd.gate.utils.ClientDisconnectionReason;

public interface IUser {

	ISession getOuterSession();

	InnerServer getInnerServer();

	void disconnect(ClientDisconnectionReason reason);

	boolean isConnected();

	String getIpAddress();

	boolean isTimeout();
}