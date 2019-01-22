/**
 * udp for kcp
 */
package com.dd.gate.services.udp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class KcpOnUdp {
    private static final Logger logger = LoggerFactory.getLogger(KcpOnUdp.class);
    private final Kcp kcp;// kcp的状态
    private final Queue<ByteBuf> received;// 输入
    private final Queue<ByteBuf> sendList;
    private final KcpListerner listerner;
    private final SocketAddress remote;// 远程地址
    private final SocketAddress local;// 本地
    private long timeout;// 超时设定
    private long lastTime;// 上次超时检查时间
    private int errcode;// 错误代码
    private volatile boolean needUpdate;
    private volatile boolean closed;

    /**
     * kcp for udp
     *
     * @param out       输出接口
     * @param remote    远程地址
     * @param local     本地地址
     * @param listerner 监听
     */
    public KcpOnUdp(Output out, SocketAddress remote, SocketAddress local, KcpListerner listerner) {
        this.listerner = listerner;
        kcp = new Kcp(out, remote);
        received = new LinkedBlockingQueue<>();
        sendList = new LinkedBlockingQueue<>();
        this.remote = remote;
        this.local = local;
    }

    /**
     * fastest: ikcp_nodelay(kcp, 1, 20, 2, 1) nodelay: 0:disable(default),
     * 1:enable interval: internal update timer interval in millisec, default is
     * 100ms resend: 0:disable fast resend(default), 1:enable fast resend nc:
     * 0:normal congestion control(default), 1:disable congestion control
     *
     * @param nodelay
     * @param interval
     * @param resend
     * @param nc
     */
    public void noDelay(int nodelay, int interval, int resend, int nc) {
        this.kcp.noDelay(nodelay, interval, resend, nc);
    }

    /**
     * set maximum window size: sndwnd=32, rcvwnd=32 by default
     *
     * @param sndwnd
     * @param rcvwnd
     */
    public void wndSize(int sndwnd, int rcvwnd) {
        this.kcp.wndSize(sndwnd, rcvwnd);
    }

    /**
     * change MTU size, default is 1400
     *
     * @param mtu
     */
    public void setMtu(int mtu) {
        this.kcp.setMtu(mtu);
    }

    public int getConv() {
        return kcp.getConv();
    }

    /**
     * conv
     *
     * @param conv
     */
    public void setConv(int conv) {
        this.kcp.setConv(conv);
    }

    /**
     * 流模式
     *
     * @return
     */
    public boolean isStream() {
        return this.kcp.isStream();
    }

    /**
     * stream模式
     *
     * @param stream
     */
    public void setStream(boolean stream) {
        this.kcp.setStream(stream);
    }

    /**
     * rto设置
     *
     * @param rto
     */
    public void setMinRto(int rto) {
        this.kcp.setMinRto(rto);
    }

    /**
     * send data to addr
     *
     * @param bb
     */
    public void send(ByteBuf bb) {
        if (!closed) {
            this.sendList.add(bb);
            this.needUpdate = true;
        } else {
            bb.release();
        }
    }

    /**
     * update one kcp
     */
    void update() {
        // input
        while (!this.received.isEmpty()) {
            ByteBuf dp = null;
            try {
                dp = this.received.remove();
                if ((errcode = kcp.input(dp)) != 0) {
                    close(new IllegalStateException("input error : " + errcode));
                    return;
                }
            } finally {
                if (dp != null) {
                    dp.release();
                }
            }
        }
        // receive
        int len;
        while ((len = kcp.peekSize()) > 0) {
            ByteBuf bb = PooledByteBufAllocator.DEFAULT.buffer(len);
            try {
                if (kcp.receive(bb) > 0) {
                    this.listerner.handleReceive(bb, this);
                    this.lastTime = System.currentTimeMillis();
                }
            } finally {
                if (bb.refCnt() > 0) bb.release();
            }
        }
        // send
        while (!this.sendList.isEmpty()) {
            ByteBuf bb = null;
            try {
                bb = sendList.remove();
                if ((errcode = this.kcp.send(bb)) != 0) {
                    close(new IllegalStateException("send error : " + errcode));
                    return;
                }
            } finally {
                if (bb != null) {
                    bb.release();
                }
            }
        }
        // update kcp status
        int cur = (int) System.currentTimeMillis();
        if (this.needUpdate || cur >= kcp.getNextUpdate()) {
            kcp.update(cur);
            kcp.setNextUpdate(kcp.check(cur));
            this.needUpdate = false;
        }
        // check timeout
        if (this.timeout > 0 && lastTime > 0 && System.currentTimeMillis() - this.lastTime > this.timeout) {
            close(new IllegalStateException("time out"));
        }
    }

    /**
     * 输入
     *
     * @param content
     */
    void input(ByteBuf content) {
        if (!this.closed) {
            this.received.add(content);
            this.needUpdate = true;
        } else {
            content.release();
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public Kcp getKcp() {
        return kcp;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "local: " + local + " remote: " + remote;
    }

    /**
     * session id
     *
     * @return
     */
    public int getSessionId() {
        return kcp.getSessionId();
    }

    /**
     * session id
     *
     * @param sessionId
     */
    public void setSessionId(int sessionId) {
        kcp.setSessionId(sessionId);
    }

    /**
     * 立即更新？
     *
     * @return
     */
    boolean needUpdate() {
        return this.needUpdate;
    }

    /**
     * 监听器
     *
     * @return
     */
    public KcpListerner getListerner() {
        return listerner;
    }

    /**
     * 本地地址
     *
     * @return
     */
    public SocketAddress getLocal() {
        return local;
    }

    /**
     * 远程地址
     *
     * @return
     */
    public SocketAddress getRemote() {
        return remote;
    }

    /**
     * 释放内存
     */
    protected void release() {
        this.kcp.release();
        for (ByteBuf item : this.received) {
            try {
                item.release();
            } catch (Exception e) {
                logger.error("byte buf released error!", e);
            }
        }
        this.received.clear();
        for (ByteBuf item : this.sendList) {
            try {
                item.release();
            } catch (Exception e) {
                logger.error("byte buf released error!", e);
            }
        }
        this.sendList.clear();
    }

    public void close(Throwable t) {
        this.closed = true;
        this.release();
        if (t != null) this.listerner.handleException(t, this);
        this.listerner.handleClose(this);
    }
}
