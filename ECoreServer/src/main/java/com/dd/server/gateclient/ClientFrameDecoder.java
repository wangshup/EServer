package com.dd.server.gateclient;

import com.dd.server.io.FrameWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ClientFrameDecoder extends LengthFieldBasedFrameDecoder {

	public ClientFrameDecoder(int maxFrameLength, int lengthFieldOffset) {
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

	private FrameWrapper frameDecode(ByteBuf frame) throws Exception {
		// 数据包长度 默认4byte
		// REQUEST FRAME FORMAT
		// +----------+------------+-----------------+-------------+--------+-------------+
		// |Total size| inner cmd  | gate session id | Header size | Header | Message body|
		// | 4 bytes  |  1 byte    |     4 bytes     |   4 bytes   | data   | data        |
		// +----------+------------+-----------------+-------------+--------+-------------+		
		//
		FrameWrapper wrapper = new FrameWrapper();
		// 包总长度 4字节
		int frameLength = frame.readInt();
		// inner cmd 1字节
		byte innerCmd = frame.readByte();
		wrapper.setInnerCmd(innerCmd);
		// 网关用的sessionId 4字节
		int gateSessionId = frame.readInt();
		wrapper.setGateSessionId(gateSessionId);
		//raw size
		frameLength = frame.readInt();
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