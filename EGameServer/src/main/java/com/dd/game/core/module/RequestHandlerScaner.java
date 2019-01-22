package com.dd.game.core.module;

import com.dd.game.core.GameEngine;
import com.dd.game.core.GameExtension;
import com.dd.game.utils.ClassUtil;
import com.dd.server.annotation.MsgHandler;
import com.dd.server.annotation.MsgHandlerParse;
import com.dd.server.annotation.MultiHandler;
import com.dd.server.event.param.ServerEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class RequestHandlerScaner {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerScaner.class);
    private static final String REQUEST_HANDLER_PACKAGE = "com.dd.game.network.handler";

    private RequestHandlerScaner() {
    }

    public static void register(GameExtension extension) throws Throwable {
        ClassLoader clazzLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(GameEngine.class.getClassLoader());
            List<Class<?>> list = ClassUtil.getClassList(REQUEST_HANDLER_PACKAGE, true, null);
            for (Class<?> clazz : list) {
                if (clazz.isAnnotationPresent(MsgHandler.class)) {
                    MsgHandler handler = clazz.getAnnotation(MsgHandler.class);
                    if (handler.id().startsWith(ServerEventType.SYSTEM_EVENT)) {
                        extension.addEventHandler(handler.id(), clazz);
                    } else {
                        extension.addRequestHandler(handler.id(), clazz);
                        for (MsgHandlerParse parse : handler.parses()) {
                            if (clazz.isAnnotationPresent(MultiHandler.class))
                                extension.addMsgParseClass(handler.id() + "." + parse.id(), parse.clazz());
                            else extension.addMsgParseClass(parse.id(), parse.clazz());
                        }
                    }
                    logger.info("Register Handler {} {} {}", clazz.getSimpleName(), handler.id(), handler.name());
                }
            }
        } catch (Exception e) {
            logger.error("Register request handler error!", e);
        } finally {
            Thread.currentThread().setContextClassLoader(clazzLoader);
        }
    }
}
