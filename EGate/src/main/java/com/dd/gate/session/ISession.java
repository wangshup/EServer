package com.dd.gate.session;

import com.dd.gate.services.udp.KcpOnUdp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.ExecutorService;

public interface ISession {

    int getSessionId();

    Channel getChannel();

    void setChannel(Channel paramChannel);

    ExecutorService getExecutor();

    String getAddress();

    String getFullIpAddress();

    long getCreateTime();

    void setCreateTime(long paramLong);

    ChannelFuture send(ByteBuf msg);

    // 以下4个用于统计流量
    void addReadBytes(int len);

    void addSendBytes(int len);

    long getReadBytes();

    long getSendBytes();

    long getMaxRead();

    void setMaxRead(long maxRead);

    long getMaxSend();

    void setMaxSend(long maxSend);

    // udp
    KcpOnUdp getKcpOnUdp();

    void setKcpOnUdp(KcpOnUdp kcp);

    void closeUdp();
}