package com.dd.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerBootStrap {
    private static final Logger logger = LoggerFactory.getLogger(ServerBootStrap.class);

    public static void main(String[] args) {
        try {
            Server.getInstance().start("config/server.properties");
        } catch (Exception e) {
            logger.error("server start error", e);
            System.exit(1);
        }
        try {
            Server.getInstance().sync();
            logger.info("server stopped");
        } catch (InterruptedException e) {
            logger.error("server stopped", e);
        }
    }
}