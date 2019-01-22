package com.dd.gate.session;

import com.dd.gate.GateServer;
import com.dd.gate.entities.InnerServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class OuterSession extends AbstractSession {

    private int serverId = -1;

    public OuterSession(Channel channel) {
        super(channel);
    }

    public InnerServer getInnerServer() {
        return GateServer.getInstance().getSessionService().getInnerServer(serverId);
    }

    public int getInnerServerId() {
        return this.serverId;
    }

    public void setInnerServerId(int serverId) {
        this.serverId = serverId;
    }

    @Override
    public ChannelFuture send(ByteBuf msg) {
        if (getKcpOnUdp() != null) {
            getKcpOnUdp().send(msg);
            return null;
        } else {
            return channel.writeAndFlush(msg);
        }
    }
}