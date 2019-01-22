package com.dd.gate.entities;

import com.dd.gate.session.ISession;
import io.netty.channel.ChannelFuture;

public class InnerServer {
    private int serverId;
    private String name;
    private ServerType type;
    private ISession session;

    public InnerServer(int serverId, String name, ServerType type, ISession session) {
        this.serverId = serverId;
        this.name = name;
        this.type = type;
        this.session = session;
    }

    public int getServerId() {
        return serverId;
    }

    public String getName() {
        return name;
    }

    public ServerType getType() {
        return type;
    }

    public ISession getSession() {
        return session;
    }

    public ChannelFuture send(Object obj) {
        return session.getChannel().writeAndFlush(obj);
    }

    public enum ServerType {
        GameServer(0);

        private int type;

        ServerType(int type) {
            this.type = type;
        }

        public static ServerType valueOf(int type) {
            for (ServerType st : ServerType.values()) {
                if (st.getType() == type) return st;
            }
            return null;
        }

        public int getType() {
            return type;
        }
    }
}
