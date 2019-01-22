package com.dd.server.io;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ServerFrameDecoder extends LengthFieldBasedFrameDecoder {

	public ServerFrameDecoder(int maxFrameLength, int lengthFieldOffset) {
		super(maxFrameLength, lengthFieldOffset, 4);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null) {
			return null;
		}
		return frameDecode(frame);
	}

	@Override
	protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
		return buffer.slice(index, length);
	}

	public static FrameWrapper frameDecode(ByteBuf frame) throws Exception {
		// 数据包长度 默认4byte
		// REQUEST FRAME FORMAT
		// +----------+-------------+--------+-------------+
		// |Total size| Header size | Header | Message body|
		// | 4 bytes  | 4 bytes     | data   | data        |
		// +----------+-------------+--------+-------------+
		// 通信包依次由4段组成
		// 1）整包长度（字节数） 整型，占4字节
		// 2）包头长度（字节数） 整型，占4字节
		// 3）包头内容（字节流） 由PacketHead通过protobuf序列化实现
		// 4）包身内容（字节流）由自定义的protobuf类序列化及反序列化
		//
		FrameWrapper wrapper = new FrameWrapper();
		// 包总长度 4字节
		int frameLength = frame.readInt();
		// Header长度 4字节
		int headLength = frame.readInt();
		int readerIndex = frame.readerIndex();
		ByteBuf header = frame.retainedSlice(readerIndex, headLength);
		wrapper.setHeadFrame(header);
		frame.skipBytes(headLength);
		readerIndex = frame.readerIndex();
		// 包体长度
		int bodyLength = frameLength - headLength - 4;
		ByteBuf body = frame.retainedSlice(readerIndex, bodyLength);
		wrapper.setDataFrame(body);
		return wrapper;
	}
}