import com.dd.protobuf.CommonProtocol.PacketHead;
import com.dd.protobuf.LoginProtocol.CSLogin;
import com.dd.server.io.ServerFrameEncoder;
import com.dd.server.io.ServerMessageEncoder;
import com.dd.server.request.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf b = PooledByteBufAllocator.DEFAULT.buffer();
		CSLogin body = CSLogin.newBuilder().setDeviceId("12345678").setAppVer("1.0.1").setCountry("CN")
				.setPlatform("Win").setProtoVer(0).setZoneName("RPG1").build();
		Message m = new Message(1, "system.login", body);
		ServerFrameEncoder.frameEncode(ServerMessageEncoder.responseEncode(m), b);
		ctx.writeAndFlush(b);
	}

	/**
	 * Calls {@link ChannelHandlerContext#fireChannelInactive()} to forward to the
	 * next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
	 *
	 * Sub-classes may override this method to change behavior.
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelInactive();
	}

	/**
	 * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward to the
	 * next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
	 *
	 * Sub-classes may override this method to change behavior.
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ctx.fireChannelRead(msg);
	}
}
