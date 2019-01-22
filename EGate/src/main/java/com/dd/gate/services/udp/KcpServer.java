/**
 * kcp服务器
 */
package com.dd.gate.services.udp;


import com.dd.gate.services.AbstractService;
import com.dd.gate.services.ServiceType;
import com.dd.gate.utils.ServerThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public abstract class KcpServer extends AbstractService implements Output, KcpListerner {
    private NioDatagramChannel channel;
    private SocketAddress addr;
    private int nodelay;
    private int interval = Kcp.IKCP_INTERVAL;
    private int resend;
    private int nc;
    private int sndwnd = Kcp.IKCP_WND_SND;
    private int rcvwnd = Kcp.IKCP_WND_RCV;
    private int mtu = Kcp.IKCP_MTU_DEF;
    private boolean stream;
    private int minRto = Kcp.IKCP_RTO_MIN;
    private KcpThread[] workers;
    private volatile boolean running;
    private long timeout;
    private EventLoopGroup bossGroup;

    public KcpServer() {
        super(ServiceType.UDP);
    }

    /**
     * server
     *
     * @param port
     * @param workerSize
     */
    public void start(String ip, int port, int workerSize) {
        if (port <= 0 || workerSize <= 0) {
            throw new IllegalArgumentException("paramers illegal");
        }
        this.workers = new KcpThread[workerSize];
        bossGroup = new NioEventLoopGroup(1, new ServerThreadFactory("Netty-Boss-UDP"));
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioDatagramChannel.class);
        bootstrap.group(bossGroup);
        bootstrap.handler(new ChannelInitializer<NioDatagramChannel>() {

            @Override
            protected void initChannel(NioDatagramChannel ch) throws Exception {
                ChannelPipeline cp = ch.pipeline();
                cp.addLast(new KcpServer.UdpHandler());
            }
        });
        ChannelFuture sync = ip == null ? bootstrap.bind(port).syncUninterruptibly() : bootstrap.bind(ip, port).syncUninterruptibly();
        channel = (NioDatagramChannel) sync.channel();
        addr = channel.localAddress();
        startWorkers();
    }

    public void stop() {
        try {
            ChannelFuture f = this.close();
            if (f != null) {
                f.awaitUninterruptibly();
            }
        } finally {
            this.bossGroup.shutdownGracefully();
        }
    }

    /**
     * 开始
     */
    private void startWorkers() {
        if (!this.running) {
            this.running = true;
            for (int i = 0; i < this.workers.length; i++) {
                workers[i] = new KcpThread(this, this, addr);
                workers[i].setName("udp-thread-" + i);
                workers[i].wndSize(sndwnd, rcvwnd);
                workers[i].noDelay(nodelay, interval, resend, nc);
                workers[i].setMtu(mtu);
                workers[i].setTimeout(timeout);
                workers[i].setMinRto(minRto);
                workers[i].setStream(stream);
                workers[i].start();
            }
        }
    }

    /**
     * close
     *
     * @return
     */
    private ChannelFuture close() {
        if (this.running) {
            this.running = false;
            for (KcpThread kt : this.workers) {
                kt.close();
            }
            this.workers = null;
            return this.channel.close();
        }
        return null;
    }

    /**
     * 连接 一旦连接上一个默认地址,则不会再收取其它地址的信息
     *
     * @param addr
     */
    public void connect(SocketAddress addr) {
        if (!this.running) {
            this.channel.connect(addr);
        }
    }

    /**
     * kcp call
     *
     * @param msg
     * @param kcp
     * @param user
     */
    @Override
    public void out(ByteBuf msg, Kcp kcp, Object user) {
        DatagramPacket temp = new DatagramPacket(msg, (InetSocketAddress) user, (InetSocketAddress) addr);
        this.channel.writeAndFlush(temp);
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
        this.nodelay = nodelay;
        this.interval = interval;
        this.resend = resend;
        this.nc = nc;
    }

    /**
     * set maximum window size: sndwnd=32, rcvwnd=32 by default
     *
     * @param sndwnd
     * @param rcvwnd
     */
    public void wndSize(int sndwnd, int rcvwnd) {
        this.sndwnd = sndwnd;
        this.rcvwnd = rcvwnd;
    }

    /**
     * change MTU size, default is 1400
     *
     * @param mtu
     */
    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    public boolean isStream() {
        return stream;
    }

    /**
     * stream mode
     *
     * @param stream
     */
    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public void setMinRto(int minRto) {
        this.minRto = minRto;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * 发送
     *
     * @param bb
     * @param ku
     */
    public void send(ByteBuf bb, KcpOnUdp ku) {
        ku.send(bb);
    }

    /**
     * receive DatagramPacket
     *
     * @param dp
     */
    private void onReceive(DatagramPacket dp) {
        if (this.running) {
            SocketAddress sender = dp.sender();
            int hash = Math.abs(sender.hashCode());
            this.workers[hash % workers.length].input(dp);
        } else {
            dp.release();
        }
    }

    /**
     * handler
     */
    public class UdpHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            DatagramPacket dp = (DatagramPacket) msg;
            KcpServer.this.onReceive(dp);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            KcpServer.this.handleException(cause, null);
        }
    }
}
