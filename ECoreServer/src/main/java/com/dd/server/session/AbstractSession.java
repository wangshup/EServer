package com.dd.server.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.RandomUtils;

import com.dd.server.udp.KcpOnUdp;
import com.dd.server.utils.Constants;

public abstract class AbstractSession implements ISession {
    private static final AtomicInteger SESSION_ID_GENERATOR = new AtomicInteger(0);
    protected int sessionId;
    private String reconnectionToken;
    protected Map<String, Object> properties;
    private long lastReadTime;
    protected long createTime;

    private long readbytes;
    private long sendbytes;
    private long maxSend;
    private long maxRead;
    private volatile KcpOnUdp kcp;

    public static volatile AtomicLong totalRead = new AtomicLong(0);
    public static volatile AtomicLong totalSend = new AtomicLong(0);

    private static int newSessionId() {
        return SESSION_ID_GENERATOR.incrementAndGet();
    }

    public AbstractSession() {
        this.properties = new ConcurrentHashMap<>();
        this.sessionId = newSessionId();
        long currTime = System.currentTimeMillis();
        this.lastReadTime = currTime;
        this.reconnectionToken = "---";
        setCreationTime(currTime);
    }

    public int getSessionId() {
        return this.sessionId;
    }

    public Object getProperty(String key) {
        return this.properties.get(key);
    }

    public void setProperty(String key, Object property) {
        this.properties.put(key, property);
    }

    public void removeProperty(String key) {
        this.properties.remove(key);
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() - this.lastReadTime > Constants.SESSION_INVALID_TIME_SEC * 1000;
    }

    public void updateLastReadTime() {
        this.lastReadTime = System.currentTimeMillis();
    }

    public long getLastReadTime() {
        return this.lastReadTime;
    }

    public void setReconnectionToken(String sessionToken) {
        this.reconnectionToken = sessionToken;
    }

    public String getReconnectionToken() {
        return this.reconnectionToken;
    }

    public long getCreationTime() {
        return this.createTime;
    }

    public void setCreationTime(long creationTime) {
        this.createTime = creationTime;
    }

    // 以下4个用于统计流量
    public void addReadBytes(int len) {
        readbytes += len;
        totalRead.addAndGet(len);
    }

    public void addSendBytes(int len) {
        sendbytes += len;
        totalSend.addAndGet(len);
    }

    public long getReadBytes() {
        return readbytes;
    }

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
}