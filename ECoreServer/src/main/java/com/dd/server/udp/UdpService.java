/**
 * udp service
 * @author wangsp
 */
package com.dd.server.udp;

import com.dd.server.annotation.ServiceStart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.server.Server;
import com.dd.server.exceptions.ServiceInitException;
import com.dd.server.exceptions.ServiceStartException;
import com.dd.server.exceptions.ServiceStopException;
import com.dd.server.io.FrameWrapper;
import com.dd.server.io.ServerFrameDecoder;
import com.dd.server.request.Request;
import com.dd.server.session.ISession;

import io.netty.buffer.ByteBuf;

@ServiceStart
public class UdpService extends KcpServer {
    private static final Logger logger = LoggerFactory.getLogger(UdpService.class);

    @Override
    public void handleReceive(ByteBuf buf, KcpOnUdp kcp) {
        try {
            FrameWrapper frame = ServerFrameDecoder.frameDecode(buf);
            Request request = new Request(frame);
            Server server = Server.getInstance();
            ISession session = server.getSessionService().getSession(kcp.getSessionId());
            session.updateLastReadTime();
            request.setSession(session);
            session.getExecutor().submit(() -> {
                try {
                    server.getRequestHandlerService().handleRequest(request);
                } catch (Exception ee) {
                    logger.error("handle request error, request:{}", request, ee);
                }
            });
        } catch (Exception e) {
            logger.error("handle udp msg error!", e);
        }
    }

    @Override
    public void handleException(Throwable ex, KcpOnUdp kcp) {
        logger.error("handle udp package error!", ex);
    }

    @Override
    public void handleClose(KcpOnUdp kcp) {
        Server server = Server.getInstance();
        ISession session = server.getSessionService().getSession(kcp.getSessionId());
        if (session != null)
            session.setKcpOnUdp(null);
        logger.info("udp disconnect!!! {}", session);
    }

    @Override
    protected void initService() throws ServiceInitException {
        int rto = Server.getInstance().getConfiguration().getInt("udp.rto", 10);
        int win = Server.getInstance().getConfiguration().getInt("udp.win", 64);
        int mtu = Server.getInstance().getConfiguration().getInt("udp.mtu", 512);
        int timeout = Server.getInstance().getConfiguration().getInt("udp.timeout", 10);
        noDelay(1, 10, 2, 1);
        setMinRto(rto);
        wndSize(win, win);
        setTimeout(timeout * 1000);
        setMtu(mtu);
    }

    @Override
    protected void startService() throws ServiceStartException {
        int port = Server.getInstance().getConfiguration().getInt("udp.port", 9033);
        int threads = Server.getInstance().getConfiguration().getInt("udp.threads", 8);
        String ip = Server.getInstance().getConfiguration().getString("udp.ip", null);
        start(ip, port, threads);
    }

    @Override
    protected void stopService() throws ServiceStopException {
        this.stop();
    }
}
