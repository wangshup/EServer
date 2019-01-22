package com.dd.server.services;

import com.dd.server.Server;
import com.dd.server.Server.ServerMode;
import com.dd.server.annotation.ServiceStart;
import com.dd.server.exceptions.ServiceInitException;
import com.dd.server.exceptions.ServiceStartException;
import com.dd.server.exceptions.ServiceStopException;
import com.dd.server.gateclient.NettyBasedClientService;

@ServiceStart
public class NettyBasedNetworkService extends AbstractService {
	private INetworkService service;

	public NettyBasedNetworkService() {
		super(ServiceType.NETWORK);
		ServerMode mode = Server.getInstance().getMode();
		service = mode == ServerMode.SERVER ? new NettyBasedServerService() : new NettyBasedClientService();
	}

	@Override
	public void initService() throws ServiceInitException {
		try {
			service.initService();
		} catch (Exception e) {
			throw new ServiceInitException(getName(), e);
		}
	}

	@Override
	public void startService() throws ServiceStartException {
		try {
			service.startService();
		} catch (Exception e) {
			throw new ServiceStartException(getName(), e);
		}
	}

	@Override
	public void stopService() throws ServiceStopException {
		try {
			service.stopService();
		} catch (Exception e) {
			throw new ServiceStopException(getName(), e);
		}
	}
}