package com.dd.game.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.game.utils.ServerStartStatus;
import com.dd.server.extensions.BaseExtension;

public class GameExtension extends BaseExtension {
    private static final Logger logger = LoggerFactory.getLogger(GameExtension.class);

    /**
     * 服务器启动时初始化: 1,配置服务端事件处理线程的数量 2,注册事件和命令处理器
     */
    public void init() {
        String zoneName = getParentZone().getName();
        try {
            GameEngine.getInstance().init(this);
            ServerStartStatus serverStartStatus = ServerStartStatus.getCurrent();
            if (serverStartStatus.isValid() && !serverStartStatus.isMacEqualsLocal() && serverStartStatus.isRunning()) {
                logger.error("server start but server is running by : " + serverStartStatus.getStartIp());
                System.setProperty("isShutDown", "0");
                Runtime.getRuntime().exit(0);
            } else {
                serverStartStatus.setStart();
                serverStartStatus.update();
            }
            logger.info("{} started", zoneName);
        } catch (Throwable e) {
            logger.error(String.format("init zone: %s fail", zoneName), e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        String isShutDown = System.getProperty("isShutDown");
        if (isShutDown != null && isShutDown.equals("0")) {
            System.setProperty("isShutDown", "");
            return;
        }
        GameEngine.getInstance().shutdown();
        trace("Game extension is destroy");
    }
}
