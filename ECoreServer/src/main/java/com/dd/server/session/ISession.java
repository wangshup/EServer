package com.dd.server.session;

import java.util.concurrent.ExecutorService;

import com.dd.server.request.Response;
import com.dd.server.udp.KcpOnUdp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public interface ISession {

    int getSessionId();

    Channel getChannel();
    
    ExecutorService getExecutor();

    String getAddress();
    
    String getFullIpAddress();

    Object getProperty(String paramString);

    void setProperty(String paramString, Object paramObject);

    void removeProperty(String paramString);   

    boolean isTimeout();

    long getLastReadTime();

    void updateLastReadTime();

    String getReconnectionToken();

    void setReconnectionToken(String paramString);

    void setChannel(Channel paramChannel);

    long getCreationTime();

    void setCreationTime(long paramLong);

    public ChannelFuture writeResponse(Response msg);

    // 以下4个用于统计流量
    void addReadBytes(int len);

    void addSendBytes(int len);

    long getReadBytes();

    long getSendBytes();

    void setMaxRead(long maxRead);

    long getMaxRead();

    void setMaxSend(long maxSend);

    long getMaxSend();    

    // udp
    KcpOnUdp getKcpOnUdp();

    void setKcpOnUdp(KcpOnUdp kcp);
}