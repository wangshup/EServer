package com.dd.gate.io;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class OuterFrameDecoder extends LengthFieldBasedFrameDecoder {

	public OuterFrameDecoder(int maxFrameLength, int lengthFieldOffset) {
		super(maxFrameLength, lengthFieldOffset, 4);
	}
}