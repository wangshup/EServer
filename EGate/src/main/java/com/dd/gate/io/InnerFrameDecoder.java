package com.dd.gate.io;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class InnerFrameDecoder extends LengthFieldBasedFrameDecoder {

    public InnerFrameDecoder(int maxFrameLength, int lengthFieldOffset) {
        super(maxFrameLength, lengthFieldOffset, 4);
    }
}