package com.dd.server.session;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

import com.dd.server.Server;
import com.dd.server.io.ServerFrameEncoder;
import com.dd.server.io.ServerMessageEncoder;
import com.dd.server.request.Response;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;

public class Session extends AbstractSession {
	private Channel channel;
	private final ExecutorService executor;

	public Session(Channel channel) {
		this.channel = channel;
		this.executor = Server.getInstance().getExecutorService().getExecutor();
	}

	public Session(Channel channel, int gateSessionId) {
		this(channel);
		this.sessionId = gateSessionId;
	}

	public Channel getChannel() {
		return this.channel;
	}

	public String getAddress() {
		return ((SocketChannel) channel).remoteAddress().getAddress().getHostAddress();
	}

	public String getFullIpAddress() {
		InetSocketAddress socketAddress = ((SocketChannel) channel).remoteAddress();
		return socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();
	}

	public void setChannel(Channel channel) {
		this.channel = ((SocketChannel) channel);
	}

	public ChannelFuture writeResponse(Response msg) {
		msg.setGateSessionId(sessionId);
		if (getKcpOnUdp() != null) {
			ByteBuf out = PooledByteBufAllocator.DEFAULT.buffer();
			ServerFrameEncoder.frameEncode(ServerMessageEncoder.responseEncode(msg), out);
			getKcpOnUdp().send(out);
			return null;
		} else {
			return channel.writeAndFlush(msg);
		}
	}

	public String toString() {
		return "id: " + getSessionId() + ", ip: " + getFullIpAddress();
	}

	@Override
	public ExecutorService getExecutor() {
		return executor;
	}
}