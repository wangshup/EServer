package com.dd.server.gateclient;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.protobuf.CommonProtocol.PacketHead;
import com.dd.server.Server;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.request.Message;
import com.dd.server.request.Response;
import com.dd.server.services.INetworkService;
import com.dd.server.utils.ServerThreadFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyBasedClientService implements INetworkService {
	private static final Logger logger = LoggerFactory.getLogger(NettyBasedClientService.class);
	private List<Client> clients = new ArrayList<>();

	@Override
	public void initService() throws Exception {
		String gates = Server.getInstance().getConfiguration().getString("gate", null);
		StringTokenizer st = new StringTokenizer(gates, ",");
		while (st.hasMoreTokens()) {
			String gate = st.nextToken();
			StringTokenizer s = new StringTokenizer(gate, ":");
			clients.add(new Client(s.nextToken(), Integer.parseInt(s.nextToken())));
		}
	}

	@Override
	public void startService() throws Exception {
		for (Client c : clients) {
			c.connect();
		}
	}

	@Override
	public void stopService() throws Exception {
		for (Client c : clients) {
			c.shutdown();
		}
	}

	static class Client {
		private String ip;
		private int port;
		private Channel channel;
		private ScheduledFuture<?> pingpongTask;
		private volatile boolean shutdown = false;

		public Client(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		public void connect() {
			Executors.newSingleThreadExecutor().submit(() -> {
				EventLoopGroup group = new NioEventLoopGroup(1, new ServerThreadFactory("Netty-Client"));
				try {
					Bootstrap b = new Bootstrap();
					b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
							.handler(new ChannelInitializer<SocketChannel>() {
								@Override
								protected void initChannel(SocketChannel ch) throws Exception {
									ch.pipeline().addLast("frame decoder", new ClientFrameDecoder(4 * 1024 * 1024, 0));
									ch.pipeline().addLast("frame encoder", new ClientFrameEncoder());
									ch.pipeline().addLast("client handler", new ClientChannelHandler());
								}
							});
					ChannelFuture f = b.connect(ip, port).sync();
					channel = f.channel();
					pingpongTask = startPingPongTask();
					channel.closeFuture().sync();
				} catch (Exception e) {
					logger.error("Netty Client {} connect error!", Client.this, e);
				} finally {
					if (pingpongTask != null)
						pingpongTask.cancel(true);
					group.shutdownGracefully();
					if (!shutdown) {
						try {
							TimeUnit.SECONDS.sleep(3);
							connect();
						} catch (Exception ex) {
							logger.error("Netty Client {} reconnect error!", Client.this, ex);
						}
					}
				}
			});
		}

		public void shutdown() {
			if (channel != null) {
				shutdown = true;
				channel.close();
			}
		}

		public void send(Message msg) {
			if (channel != null && channel.isActive())
				channel.writeAndFlush(msg);
		}

		private ScheduledFuture<?> startPingPongTask() {
			return Server.getInstance().getExecutorService().getScheduleExecutor().scheduleAtFixedRate(() -> {
				PacketHead head = PacketHead.newBuilder().setActionId(ServerEventType.GATE_PINGPONG).build();
				send(new Response(head));
			}, 15, 15, TimeUnit.SECONDS);
		}

		@Override
		public String toString() {
			return "Client [ip=" + ip + ", port=" + port + "]";
		}
	}
}