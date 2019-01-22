/**
 * udp service
 *
 * @author wangsp
 */
package com.dd.gate.services.udp;


import com.dd.gate.GateServer;
import com.dd.gate.exceptions.ServiceInitException;
import com.dd.gate.exceptions.ServiceStartException;
import com.dd.gate.exceptions.ServiceStopException;
import com.dd.gate.session.ISession;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpService extends KcpServer {
    private static final Logger logger = LoggerFactory.getLogger(UdpService.class);

    @Override
    public void handleReceive(ByteBuf buf, KcpOnUdp kcp) {
        try {
            ISession session = GateServer.getInstance().getSessionService().getSession(kcp.getSessionId());
            GateServer.getInstance().getOuterHandlerService().handle(session, buf);
        } catch (Exception e) {
            logger.error("[gate] handle udp msg error!", e);
        }
    }

    @Override
    public void handleException(Throwable ex, KcpOnUdp kcp) {
        logger.error("[gate] handle udp package error!", ex);
    }

    @Override
    public void handleClose(KcpOnUdp kcp) {
        ISession session = GateServer.getInstance().getSessionService().getSession(kcp.getSessionId());
        if (session != null) session.setKcpOnUdp(null);
        logger.info("[gate] udp {} disconnect!!! ", kcp);
    }

    @Override
    protected void initService() throws ServiceInitException {
        int rto = GateServer.getInstance().getConfiguration().getInt("udp.rto", 10);
        int win = GateServer.getInstance().getConfiguration().getInt("udp.win", 64);
        int mtu = GateServer.getInstance().getConfiguration().getInt("udp.mtu", 512);
        int timeout = GateServer.getInstance().getConfiguration().getInt("udp.timeout", 10);
        noDelay(1, 10, 2, 1);
        setMinRto(rto);
        wndSize(win, win);
        setTimeout(timeout * 1000);
        setMtu(mtu);
    }

    @Override
    protected void startService() throws ServiceStartException {
        int port = GateServer.getInstance().getConfiguration().getInt("udp.port", 9033);
        int threads = GateServer.getInstance().getConfiguration().getInt("udp.threads", 8);
        String ip = GateServer.getInstance().getConfiguration().getString("udp.ip", null);
        start(ip, port, threads);
    }

    @Override
    protected void stopService() throws ServiceStopException {
        this.stop();
    }
}
