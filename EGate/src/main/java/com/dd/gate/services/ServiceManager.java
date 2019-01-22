package com.dd.gate.services;

import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.gate.exceptions.ServiceInitException;
import com.dd.gate.exceptions.ServiceStartException;
import com.dd.gate.exceptions.ServiceStopException;
import com.dd.gate.utils.JarFilesUtil;

public class ServiceManager {
	protected static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
	private Map<ServiceType, IService> services = new EnumMap<>(ServiceType.class);

	public void init() throws ServiceInitException {
		List<Class<?>> list = JarFilesUtil.getClassList("com.dd.gate.services", true, null,
				getClass().getClassLoader());
		for (Class<?> clazz : list) {
			try {
				if (AbstractService.class.isAssignableFrom(clazz) && clazz != AbstractService.class
						&& !Modifier.isAbstract(clazz.getModifiers())) {
					IService service = (IService) clazz.newInstance();
					services.put(service.getServiceType(), service);
				}
			} catch (Exception e) {
				logger.error("[gate] init service {} error!", clazz.getName(), e);
			}
		}
		for (IService service : services.values()) {
			service.init();
		}
	}

	public void start() throws ServiceStartException {
		for (IService service : services.values()) {
			service.start();
		}
	}

	public void stop() throws ServiceStopException {
		for (IService service : services.values()) {
			service.stop();
		}
	}

	public IService getService(ServiceType serviceType) {
		if (services.containsKey(serviceType))
			return services.get(serviceType);
		return null;
	}
}