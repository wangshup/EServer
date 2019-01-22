package com.dd.server.services;

public interface INetworkService {

	void initService() throws Exception;

	void startService() throws Exception;

	void stopService() throws Exception;
}