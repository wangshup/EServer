package com.dd.gate.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;

import com.dd.gate.GateServer;
import com.dd.gate.exceptions.ServiceInitException;
import com.dd.gate.exceptions.ServiceStartException;
import com.dd.gate.exceptions.ServiceStopException;
import com.dd.gate.io.OuterChannelHandler;
import com.dd.gate.io.OuterFrameDecoder;
import com.dd.gate.utils.ServerThreadFactory;
import com.google.common.collect.ImmutableList;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NetworkOuterService extends AbstractService {
	private List<Integer> ports;
	private String ip;
	private List<Channel> serverChannels;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	public NetworkOuterService() {
		super(ServiceType.NETWORK_OUTER);
	}

	protected void initService() throws ServiceInitException {
		ip = GateServer.getInstance().getConfiguration().getString("outer.ip", null);
		String port = GateServer.getInstance().getConfiguration().getString("outer.port");
		if (StringUtils.isBlank(port)) {
			throw new IllegalArgumentException("listen port config error");
		}
		String[] portArr = StringUtils.split(port, '|');
		List<Integer> tmp = new ArrayList<Integer>();
		for (String p : portArr) {
			tmp.add(Integer.valueOf(Integer.parseInt(p)));
		}
		this.ports = ImmutableList.copyOf(tmp);
	}

	protected void startService() throws ServiceStartException {
		this.bossGroup = new NioEventLoopGroup(ports.size(), new ServerThreadFactory("Netty-Boss-Outer"));
		this.workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(),
				new ServerThreadFactory("Netty-Work-Outer"));

		try {
			ServerBootstrap boot = new ServerBootstrap();
			boot.group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ServerHandlerInitializer());
			Configuration configuration = GateServer.getInstance().getConfiguration();

			boot.option(ChannelOption.SO_REUSEADDR, true)
					.option(ChannelOption.SO_BACKLOG, configuration.getInt("outer.net.backlog", 1024))
					.option(ChannelOption.SO_RCVBUF, configuration.getInt("outer.net.rcvbuf", 256 * 1024))
					.option(ChannelOption.SO_SNDBUF, configuration.getInt("outer.net.sndbuf", 256 * 1024))
					.childOption(ChannelOption.TCP_NODELAY, true);

			this.serverChannels = new ArrayList<>(this.ports.size());
			if (ip != null) {
				for (Integer port : this.ports) {
					Channel f = boot.bind(ip, port.intValue()).sync().channel();
					this.serverChannels.add(f);
				}
			} else {
				for (Integer port : this.ports) {
					Channel f = boot.bind(port.intValue()).sync().channel();
					this.serverChannels.add(f);
				}
			}
		} catch (InterruptedException e) {
			throw new ServiceStartException(getName(), e);
		}
	}

	protected void stopService() throws ServiceStopException {
		try {
			for (Channel channel : this.serverChannels) {
				ChannelFuture f = channel.close();
				f.awaitUninterruptibly();
			}
		} finally {
			try {
				workerGroup.shutdownGracefully().sync();
				bossGroup.shutdownGracefully().sync();
			} catch (Exception e) {
				logger.warn("[gate] shutdown worker group or boss group error!", e);
			}
		}
	}

	static class ServerHandlerInitializer extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			Configuration configuration = GateServer.getInstance().getConfiguration();
			int maxRequestMsgSize = configuration.getInt("outer.msg.max.size", 4 * 1024 * 1024);
			ChannelPipeline p = ch.pipeline();
			p.addLast("FrameDecoder", new OuterFrameDecoder(maxRequestMsgSize, 0));
			p.addLast("ReadTimeout", new ReadTimeoutHandler(330));
			p.addLast("ServerHandler", new OuterChannelHandler());
		}
	}
}