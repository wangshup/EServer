package com.dd.server.event;

import com.dd.protobuf.HeartBeatProtocol;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.request.Request;
import com.dd.server.request.Response;
import com.dd.server.session.ISession;

public class PingPongSystemHandler extends AbstractSystemRequestHandler {
    public PingPongSystemHandler() {
        super(ServerEventType.PINGPONG);
    }

    public boolean validate(Request request) {
        return true;
    }

    public void execute(Request request) throws Exception {
        ISession sender = request.getSession();
        HeartBeatProtocol.SCHeartBeat.Builder builder = HeartBeatProtocol.SCHeartBeat.newBuilder();
        builder.setServerTime(String.valueOf(System.currentTimeMillis()));
        Response.sendEvent(ServerEventType.PINGPONG, builder.build(), sender);
    }
}