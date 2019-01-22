package com.dd.gate;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.gate.services.DataService;
import com.dd.gate.services.GeoIPService;
import com.dd.gate.services.IExecutorService;
import com.dd.gate.services.InnerHandlerService;
import com.dd.gate.services.OuterHandlerService;
import com.dd.gate.services.ServiceManager;
import com.dd.gate.services.ServiceType;
import com.dd.gate.services.SessionService;
import com.dd.gate.utils.AsciiArt;
import com.dd.gate.utils.FileListener;
import com.dd.gate.utils.FileMonitor;
import com.dd.gate.utils.MonitoredFile;

public final class GateServer {
	private final static Logger logger = LoggerFactory.getLogger(GateServer.class);
	private ServiceManager serviceManager;
	private Configuration configuration;
	private CountDownLatch latch;
	private final long serverStartTime;
	private static final GateServer instance = new GateServer();

	private GateServer() {
		this.latch = new CountDownLatch(1);
		this.serverStartTime = System.currentTimeMillis();
	}

	public long getServerUpTime() {
		return System.currentTimeMillis() - serverStartTime;
	}

	public void start(final String config) throws Exception {
		long start = System.currentTimeMillis();
		logger.info("[gate] Gate server starting...");
		final File configFile = new File(config);
		loadConfiguration(configFile);
		String startMessage = AsciiArt.getAsciiMessage("start");
		if (StringUtils.isNotEmpty(startMessage)) {
			logger.info(startMessage);
		}

		registerShutdownHook();
		this.serviceManager = new ServiceManager();
		this.serviceManager.init();
		this.serviceManager.start();
		getExecutorService().getScheduleExecutor()
				.scheduleAtFixedRate(new FileMonitor(configFile, true, new FileListener() {
					@Override
					public void onChange(Iterable<MonitoredFile> changedList) {
						try {
							MonitoredFile file = changedList.iterator().next();
							logger.info("[gate] config file changed for {} ", file.getGroup());
							loadConfiguration(file.getFile());
						} catch (Exception e) {
							logger.error("[gate] reload config file error!", e);
						}
					}
				}), 1, 1, TimeUnit.MINUTES);
		String readyAsciiMessage = AsciiArt.getAsciiMessage("ready");
		if (StringUtils.isNotEmpty(readyAsciiMessage)) {
			logger.info("{}[ {} ]\n", readyAsciiMessage, "1.0.1");
		}
		logger.info("[gate] Gate Server ({}) READY![use time {}s]", "1.0.1", (System.currentTimeMillis() - start) / 1000);
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
			logger.error("[gate] stop core services", e);
		} finally {
			this.latch.countDown();
		}
	}
	
	public IExecutorService getExecutorService() {
		return getService(ServiceType.EXECUTOR);
	}

	public DataService getDataService() {
		return getService(ServiceType.DATA);
	}

	public OuterHandlerService getOuterHandlerService() {
		return getService(ServiceType.OUTER_HANDLER);
	}

	public InnerHandlerService getInnerHandlerService() {
		return getService(ServiceType.INNER_HANDLER);
	}

	public SessionService getSessionService() {
		return getService(ServiceType.SESSION);
	}

	public GeoIPService getGeoIPService() {
		return getService(ServiceType.GEOIP);
	}

	@SuppressWarnings("unchecked")
	public <T> T getService(ServiceType type) {
		return (T) serviceManager.getService(type);
	}

	public Configuration getConfiguration() {
		return this.configuration;
	}

	public static GateServer getInstance() {
		return instance;
	}

	public void sync() throws InterruptedException {
		this.latch.await();
	}

	static class ServerShutdownHook extends Thread {
		private Logger logger = LoggerFactory.getLogger(ServerShutdownHook.class);

		public ServerShutdownHook() {
			super("Gate server shutdown hook");
			this.logger.info("[gate] Gate server shutdown hook started");
		}

		public void run() {
			try {
				GateServer.getInstance().stop();
				this.logger.error("[gate] Gate server stop successfully");
			} catch (Throwable t) {
				this.logger.error("[gate] Gate server stop error", t);
			}
		}
	}
}