package com.dd.gate.session;

import com.dd.gate.services.udp.KcpOnUdp;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractSession implements ISession {
    private static final AtomicInteger SESSION_ID_GENERATOR = new AtomicInteger(0);
    public static volatile AtomicLong totalRead = new AtomicLong(0);
    public static volatile AtomicLong totalSend = new AtomicLong(0);
    protected long createTime;
    protected Channel channel;
    private int sessionId;
    private long readbytes;
    private long sendbytes;
    private long maxSend;
    private long maxRead;
    private volatile KcpOnUdp kcp;

    public AbstractSession(Channel channel) {
        this.sessionId = newSessionId();
        this.channel = channel;
        setCreateTime(System.currentTimeMillis());
    }

    private static int newSessionId() {
        return SESSION_ID_GENERATOR.incrementAndGet();
    }

    @Override
    public int getSessionId() {
        return this.sessionId;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = ((SocketChannel) channel);
    }

    @Override
    public String getAddress() {
        return ((SocketChannel) channel).remoteAddress().getAddress().getHostAddress();
    }

    @Override
    public String getFullIpAddress() {
        InetSocketAddress socketAddress = ((SocketChannel) channel).remoteAddress();
        return socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();
    }

    @Override
    public ExecutorService getExecutor() {
        return channel.eventLoop();
    }

    @Override
    public String toString() {
        return "id: " + getSessionId() + ", ip: " + getFullIpAddress();
    }

    @Override
    public long getCreateTime() {
        return this.createTime;
    }

    @Override
    public void setCreateTime(long creationTime) {
        this.createTime = creationTime;
    }

    // 以下4个用于统计流量
    @Override
    public void addReadBytes(int len) {
        readbytes += len;
        totalRead.addAndGet(len);
    }

    @Override
    public void addSendBytes(int len) {
        sendbytes += len;
        totalSend.addAndGet(len);
    }

    @Override
    public long getReadBytes() {
        return readbytes;
    }

    @Override
    public long getSendBytes() {
        return sendbytes;
    }

    @Override
    public long getMaxSend() {
        return maxSend;
    }

    @Override
    public void setMaxSend(long maxSend) {
        this.maxSend = maxSend;
    }

    @Override
    public long getMaxRead() {
        return maxRead;
    }

    @Override
    public void setMaxRead(long maxRead) {
        this.maxRead = maxRead;
    }

    @Override
    public KcpOnUdp getKcpOnUdp() {
        return kcp;
    }

    @Override
    public void setKcpOnUdp(KcpOnUdp kcp) {
        this.kcp = kcp;
    }

    @Override
    public void closeUdp() {
        if (kcp != null) kcp.close(null);
        kcp = null;
    }
}