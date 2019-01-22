package com.dd.server.services;

import com.dd.server.Server;
import com.dd.server.annotation.ServiceStart;
import com.dd.server.entities.IUser;
import com.dd.server.exceptions.*;
import com.dd.server.extensions.IExtension;
import com.dd.server.request.Request;
import com.dd.server.request.SystemRequestHandler;
import com.dd.server.session.ISession;
import io.netty.channel.Channel;

import java.util.concurrent.TimeUnit;

@ServiceStart
public class RequestHandlerService extends AbstractService {
    public RequestHandlerService() {
        super(ServiceType.REQUEST_HANDLER);
    }

    public void handleRequest(final Request request) {
        ISession session = request.getSession();
        session.getExecutor().execute(() -> {
            try {
                long startTime;
                request.setHandleStartTime(startTime = System.currentTimeMillis());
                request.decode();
                if (request.isGameQuest()) {
                    handleGameRequest(request);
                } else {
                    handleServerEvent(request);
                }
                long now = System.currentTimeMillis();
                long totalTime = now - request.getReceiveTime();
                long handleTime = now - startTime;
                long maxWaitTime = Server.getInstance().getConfiguration().getLong("request.max_wait_millis", TimeUnit.MINUTES.toMillis(1));
                long maxHandleTime = Server.getInstance().getConfiguration().getInt("request.slow.millis", 1000);
                if (handleTime > maxHandleTime || totalTime > maxWaitTime) {
                    long waitTime = startTime - request.getReceiveTime();
                    logger.warn("[SLOW_REQ] WaitTime: {} HandleTime: {} TotalTime: {}, REQ: {}", waitTime, handleTime, totalTime, request);
                }
            } catch (Exception e) {
                if (Server.getInstance().getMode() == Server.ServerMode.SERVER) {
                    session.getChannel().pipeline().fireExceptionCaught(e);
                    logger.error("{} handler error,channel will be closed!!", request, e);
                } else {
                    logger.error("{} handler error!!", request, e);
                }
            }
        });
    }

    protected void handleGameRequest(Request request) throws Exception {
        IUser user = Server.getInstance().getExtensionService().getUserBySession(request.getSession());
        if (user == null) {
            throw new RequestException("user is null, request: " + request);
        }

        IExtension extension = user.getZone().getRunningExtension();
        if (extension == null) {
            throw new ExtensionException("extension is null");
        }
        extension.handleRequest(user, request);
    }

    protected void handleServerEvent(Request request) throws Exception {
        ISession session = request.getSession();
        if (session == null) {
            logger.error("session is null when handle request {}", request);
            return;
        }

        Channel channel = session.getChannel();
        if (channel == null || !channel.isActive()) {
            logger.error("channel is unactive when handle request {}", request);
            return;
        }

        SystemRequestHandler.handleRequest(request);
    }

    @Override
    protected void initService() throws ServiceInitException {
    }

    @Override
    protected void startService() throws ServiceStartException {
    }

    @Override
    protected void stopService() throws ServiceStopException {
    }
}