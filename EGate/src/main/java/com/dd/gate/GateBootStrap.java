package com.dd.gate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateBootStrap {
    private static final Logger logger = LoggerFactory.getLogger(GateBootStrap.class);

    public static void main(String[] args) {
        try {
            GateServer.getInstance().start("config/gate.properties");
        } catch (Exception e) {
            logger.error("[gate] server start error", e);
            System.exit(1);
        }
        try {
            GateServer.getInstance().sync();
            logger.info("[gate] server stopped");
        } catch (InterruptedException e) {
            logger.error("[gate] server stopped", e);
        }
    }
}