package com.dd.server;

import com.dd.server.mq.MQService;
import com.dd.server.services.*;
import com.dd.server.utils.AsciiArt;
import com.dd.server.utils.FileListener;
import com.dd.server.utils.FileMonitor;
import com.dd.server.utils.MonitoredFile;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class Server {
    private final static Logger logger = LoggerFactory.getLogger(Server.class);
    private static final Server instance = new Server();
    private final long serverStartTime;
    private ServiceManager serviceManager;
    private Configuration configuration;
    private CountDownLatch latch;
    private ServerMode mode;

    private Server() {
        this.latch = new CountDownLatch(1);
        this.serverStartTime = System.currentTimeMillis();
    }

    public static Server getInstance() {
        return instance;
    }

    public long getServerUpTime() {
        return System.currentTimeMillis() - serverStartTime;
    }

    public void start(final String config) throws Exception {
        long start = System.currentTimeMillis();
        logger.info("EServer starting...");
        final File configFile = new File(config);
        loadConfiguration(configFile);
        mode = "gate".equals(configuration.getString("server.mode", "server")) ? ServerMode.GATE : ServerMode.SERVER;
        String startMessage = AsciiArt.getAsciiMessage("start");
        if (StringUtils.isNotEmpty(startMessage)) {
            logger.info(startMessage);
        }

        registerShutdownHook();
        this.serviceManager = new ServiceManager();
        this.serviceManager.init();
        this.serviceManager.start();
        getExecutorService().getScheduleExecutor().scheduleAtFixedRate(new FileMonitor(configFile, true, new FileListener() {
            @Override
            public void onChange(Iterable<MonitoredFile> changedList) {
                try {
                    MonitoredFile file = changedList.iterator().next();
                    logger.info("config file changed for {} ", file.getGroup());
                    loadConfiguration(file.getFile());
                } catch (Exception e) {
                    logger.error("reload config file error!", e);
                }
            }
        }), 1, 1, TimeUnit.MINUTES);
        String readyAsciiMessage = AsciiArt.getAsciiMessage("ready");
        if (StringUtils.isNotEmpty(readyAsciiMessage)) {
            logger.info("{}[ {} ]\n", readyAsciiMessage, "1.0.1");
        }
        logger.info("EServer ({}) READY![use time {}s]", "1.0.1", (System.currentTimeMillis() - start) / 1000);
    }

    private void loadConfiguration(File file) throws ConfigurationException {
        CompositeConfiguration configuration = new CompositeConfiguration();
        configuration.addConfiguration(new SystemConfiguration());
        Configurations configs = new Configurations();
        configuration.addConfiguration(configs.properties(file));
        this.configuration = configuration;
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new ServerShutdownHook());
    }

    public void stop() {
        try {
            serviceManager.stop();
        } catch (Exception e) {
            logger.error("stop core services", e);
        } finally {
            this.latch.countDown();
        }
    }

    public IExecutorService getExecutorService() {
        return getService(ServiceType.EXECUTOR);
    }

    public RequestHandlerService getRequestHandlerService() {
        return getService(ServiceType.REQUEST_HANDLER);
    }

    public ExtensionService getExtensionService() {
        return getService(ServiceType.EXTENSION);
    }

    public SessionService getSessionService() {
        return getService(ServiceType.SESSION);
    }

    public MQService getMQService() {
        return getService(ServiceType.MQ);
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(ServiceType type) {
        return (T) serviceManager.getService(type);
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void sync() throws InterruptedException {
        this.latch.await();
    }

    public ServerMode getMode() {
        return mode;
    }

    public enum ServerMode {
        SERVER, GATE
    }

    static class ServerShutdownHook extends Thread {
        private Logger logger = LoggerFactory.getLogger(ServerShutdownHook.class);

        public ServerShutdownHook() {
            super("EServer shutdown hook");
            this.logger.info("EServer shutdown hook started");
        }

        public void run() {
            try {
                Server.getInstance().stop();
                this.logger.error("EServer stop successfully");
            } catch (Throwable t) {
                this.logger.error("EServer stop error", t);
            }
        }
    }
}