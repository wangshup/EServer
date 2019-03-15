package com.dd.game.core;

import com.dd.edata.EData;
import com.dd.edata.db.DBWhere;
import com.dd.game.core.config.GameConfig;
import com.dd.game.core.module.RequestHandlerScaner;
import com.dd.game.entity.ServerInfo;
import com.dd.game.entity.model.ServerInfoModel;
import com.dd.game.module.job.GameSchedule;
import com.dd.game.utils.Constants;
import com.dd.game.web.proxy.WebProxyImpl;
import com.dd.server.Server;
import com.dd.server.mq.service.MQService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class GameEngine {
    private static final Logger logger = LoggerFactory.getLogger(GameEngine.class);
    private static final GameEngine INSTANCE = new GameEngine();
    public static int ZONE_ID = 1;
    private GameExtension extension;
    private EData edata;
    private ServerInfo si;

    private GameEngine() {
    }

    public static GameEngine getInstance() {
        return INSTANCE;
    }

    public static EData getEData() {
        return INSTANCE.edata;
    }

    public void init(GameExtension extension) throws Throwable {
        this.extension = extension;
        initZoneInfo();
        GameConfig.getInstance().init(getConfigValue("config.path"));
        StopWatch watch = new StopWatch();
        watch.start();
        edata = EData.start(ZONE_ID, "com.dd.game", getClass().getClassLoader(), getConfigProperties());
        logger.info("DataServer init cost {}s", watch.getTime() / 1000);
        watch.reset();
        watch.start();
        initMqService();
        initServerInfo();
        extension.getParentZone().setProperty("webproxy", new WebProxyImpl());
        GameSchedule.getInstance().start();
        RequestHandlerScaner.register(extension);
        logger.info("All modules init cost {}s", watch.getTime() / 1000);
        watch.reset();
    }

    public void shutdown() {
        if (edata != null) {
            edata.shutdown();
        }
    }

    public GameExtension getExtension() {
        return extension;
    }

    private void initZoneInfo() {
        String zoneName = extension.getParentZone().getName();
        String zoneId = StringUtils.substring(zoneName, StringUtils.lastIndexOf(zoneName, "_") + 1);
        try {
            ZONE_ID = Integer.parseInt(zoneId);
        } catch (Exception e) {
            ZONE_ID = 0;
        }
    }

    private void initServerInfo() throws Exception {
        ServerInfoModel model = edata.select(ServerInfoModel.class, DBWhere.equal("id", ZONE_ID));
        if (model == null) {
            model = new ServerInfoModel();
            model.setId(ZONE_ID);
            model.setNextId(1000);
            model.setStartTime(System.currentTimeMillis());
            GameEngine.getEData().insert(model);
            //model.insert(false);
        }
        this.si = new ServerInfo(model);
    }

    public ServerInfo getServerInfo() {
        return si;
    }

    /**
     * 获取配置信息
     *
     * @param key
     * @return
     */
    public String getConfigValue(String key) {
        String configValue;
        if (extension != null) {
            configValue = extension.getConfigProperties().getProperty(key);
        } else {
            configValue = getConfigProperties().getProperty(key);
        }
        return configValue;
    }

    public String getConfigFileName() {
        String configFileName = "config.properties";
        if (extension != null) {
            configFileName = extension.getPropertiesFileName();
        }
        return configFileName;
    }

    public Properties getConfigProperties() {
        Properties configProperties;
        if (extension != null) {
            configProperties = extension.getConfigProperties();
        } else {
            configProperties = new Properties();
            try (InputStream is = new FileInputStream(getConfigFileName())) {
                configProperties.load(is);
            } catch (IOException e) {
                logger.error("read config properties error", e);
            }
        }
        return configProperties;
    }

    public boolean isTestServer() {
        return Boolean.parseBoolean(getConfigProperties().getProperty("server.test", "false"));
    }

    private void initMqService() throws Exception {
        MQService mq = Server.getInstance().getMQService();
        if (mq != null) {
            mq.registerConsumer(Constants.SERVER_ID);
            logger.info("mq service started");
        } else {
            logger.warn("mq service not start!!!");
        }
    }
}
