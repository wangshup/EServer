package com.dd.server.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.server.Server;
import com.dd.server.exceptions.ServiceInitException;
import com.dd.server.exceptions.ServiceStopException;
import com.dd.server.io.ServerChannelHandler;
import com.dd.server.io.ServerFrameDecoder;
import com.dd.server.io.ServerFrameEncoder;
import com.dd.server.io.ServerMessageDecoder;
import com.dd.server.io.ServerMessageEncoder;
import com.dd.server.utils.ServerThreadFactory;
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

public class NettyBasedServerService implements INetworkService {
	private static Logger logger = LoggerFactory.getLogger(NettyBasedServerService.class);
	private List<Integer> ports;
	private String ip;
	private List<Channel> serverChannels;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	@Override
	public void initService() throws ServiceInitException {
		ip = Server.getInstance().getConfiguration().getString("ip", null);
		String port = Server.getInstance().getConfiguration().getString("port");
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

	@Override
	public void startService() throws Exception {
		bossGroup = new NioEventLoopGroup(ports.size(), new ServerThreadFactory("Netty-Boss"));
		workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(),
				new ServerThreadFactory("Netty-Work"));

		ServerBootstrap boot = new ServerBootstrap();
		boot.group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ServerHandlerInitializer());
		Configuration configuration = Server.getInstance().getConfiguration();

		boot.option(ChannelOption.SO_REUSEADDR, true)
				.option(ChannelOption.SO_BACKLOG, Integer.valueOf(configuration.getInt("net.backlog", 1024)))
				.option(ChannelOption.SO_RCVBUF, Integer.valueOf(configuration.getInt("net.rcvbuf", 256 * 1024)))
				.option(ChannelOption.SO_SNDBUF, Integer.valueOf(configuration.getInt("net.sndbuf", 256 * 1024)))
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
	}

	@Override
	public void stopService() throws ServiceStopException {
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
				logger.warn("shutdown worker group or boss group error!", e);
			}
		}
	}

	static class ServerHandlerInitializer extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			Configuration configuration = Server.getInstance().getConfiguration();
			int maxRequestMsgSize = configuration.getInt("request.msg.max.size", 4 * 1024 * 1024);
			ChannelPipeline p = ch.pipeline();
			p.addLast("FrameDecoder", new ServerFrameDecoder(maxRequestMsgSize, 0));
			p.addLast("MessageDecoder", new ServerMessageDecoder());
			p.addLast("FrameEncoder", new ServerFrameEncoder());
			p.addLast("MessageEncoder", new ServerMessageEncoder());
			p.addLast("ServerHandler", new ServerChannelHandler());
		}
	}
}