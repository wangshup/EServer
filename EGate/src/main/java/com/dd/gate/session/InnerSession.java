package com.dd.gate.session;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class InnerSession extends AbstractSession {

    public InnerSession(Channel channel) {
        super(channel);
    }

    @Override
    public ChannelFuture send(ByteBuf msg) {
        return channel.writeAndFlush(msg);
    }
}