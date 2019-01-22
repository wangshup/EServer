package com.dd.gate.utils;

import com.dd.gate.GateServer;

public final class Constants {
	public static final byte CMD_INNER_TUNNEL = 1;
	public static final byte CMD_INNER_REGISTER = 2;
	public static final byte CMD_INNER_PINGPONG = 3;
    public static final byte CMD_INNER_DISCONNECT = 4;
	
    public static final String VERSION = "1.0.1";
    public static final int SESSION_TASK_INTERVAL = GateServer.getInstance().getConfiguration()
            .getInt("session.interval.seconds", 60);

    public static final int SESSION_INVALID_TIME_SEC = GateServer.getInstance().getConfiguration()
            .getInt("session.timeout.seconds", 480);

    public static final int USER_INVALID_TIME_SEC = GateServer.getInstance().getConfiguration()
            .getInt("user.timeout.seconds", 300);

    public static final int BYTES_DEBUG_THRESHOLD = GateServer.getInstance().getConfiguration()
            .getInt("bytes.debug.threshold", 512);
}