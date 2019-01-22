package com.dd.game.network.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.server.event.BaseServerEventHandler;
import com.dd.server.event.param.IServerEvent;
import com.dd.server.exceptions.ServerHandleException;
import com.dd.server.extensions.BaseExtension;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;

/**
 * 事件处理的核心类
 */
public abstract class AbstractEventHandler extends BaseServerEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(BaseExtension.class);
    protected static final JsonFormat jsonFormat = new JsonFormat();

    /**
     * 事件处理的总入口
     */
    public Message handleServerEvent(IServerEvent event) throws ServerHandleException {
        try {
            return handleEvent(event);
        } catch (ServerHandleException ex) {
            throw ex;
        } catch (Throwable e) {
            logger.error("EventHandler Error: ", e);
        }
        return null;
    }

    protected abstract Message handleEvent(IServerEvent event) throws Exception;
}
