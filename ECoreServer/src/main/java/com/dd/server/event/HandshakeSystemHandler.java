package com.dd.server.event;

import com.dd.server.event.param.ServerEventType;
import com.dd.server.exceptions.EventHandleException;
import com.dd.server.exceptions.SystemRequestValidationException;
import com.dd.server.request.Request;
import com.dd.server.request.Response;
import com.dd.server.session.ISession;

public class HandshakeSystemHandler extends AbstractSystemRequestHandler {

	public HandshakeSystemHandler() {
		super(ServerEventType.HANDSHAKE);
	}

	public boolean validate(Request request) throws SystemRequestValidationException {
		return true;
	}

	public void execute(Request request) throws EventHandleException {
	    final ISession session = request.getSession();
	    Response.sendEvent(eventType, null, session);
	}
}
