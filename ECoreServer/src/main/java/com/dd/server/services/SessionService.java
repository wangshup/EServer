package com.dd.server.services;

import com.dd.server.Server;
import com.dd.server.Server.ServerMode;
import com.dd.server.annotation.ServiceStart;
import com.dd.server.exceptions.ServiceInitException;
import com.dd.server.exceptions.ServiceStartException;
import com.dd.server.exceptions.ServiceStopException;
import com.dd.server.gateclient.GateSessionService;
import com.dd.server.session.ISession;
import com.dd.server.utils.Constants;
import io.netty.channel.Channel;

import java.util.concurrent.TimeUnit;

@ServiceStart
public class SessionService extends AbstractService implements ISessionService {
    private ISessionService sessionService;

    public SessionService() {
        super(ServiceType.SESSION);
        ServerMode mode = Server.getInstance().getMode();
        sessionService = mode == ServerMode.SERVER ? new ServerSessionService() : new GateSessionService();
    }

    @Override
    public void addSession(ISession session) {
        sessionService.addSession(session);
    }

    @Override
    public void addSession(ISession session, boolean increseCountFlag) {
        sessionService.addSession(session, increseCountFlag);
    }

    @Override
    public int getCurrentSessionsCount() {
        return sessionService.getCurrentSessionsCount();
    }

    @Override
    public long getTotalSessionsCount() {
        return sessionService.getTotalSessionsCount();
    }

    @Override
    public ISession removeSession(Channel channel) {
        return sessionService.removeSession(channel);
    }

    @Override
    public void removeSession(ISession session) {
        sessionService.removeSession(session);

    }

    @Override
    public ISession removeSession(int sessionId) {
        return sessionService.removeSession(sessionId);
    }

    @Override
    public ISession getSession(Channel channel) {
        return sessionService.getSession(channel);
    }

    @Override
    public ISession getSession(int sessionId) {
        return sessionService.getSession(sessionId);
    }

    @Override
    public boolean containsSession(ISession session) {
        return sessionService.containsSession(session);
    }

    @Override
    protected void initService() throws ServiceInitException {
    }

    @Override
    protected void startService() throws ServiceStartException {
        Server.getInstance().getExecutorService().getScheduleExecutor().scheduleAtFixedRate(() -> {
            checkUserTimeout();
            checkSessionTimeout();
        }, Constants.SESSION_TASK_INTERVAL, Constants.SESSION_TASK_INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    protected void stopService() throws ServiceStopException {
    }

    @Override
    public void checkSessionTimeout() {
        sessionService.checkSessionTimeout();
    }

    @Override
    public void checkUserTimeout() {
        sessionService.checkUserTimeout();
    }

    @Override
    public ISession getSession(Channel channel, int sessionId) {
        return sessionService.getSession(channel, sessionId);
    }
}