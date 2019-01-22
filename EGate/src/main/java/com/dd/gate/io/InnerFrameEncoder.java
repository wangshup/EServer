package com.dd.gate.io;

import com.dd.server.io.FrameWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class InnerFrameEncoder extends MessageToByteEncoder<FrameWrapper> {

    public static void frameEncode(FrameWrapper frame, ByteBuf out) {
        int headerLength = frame.getHeadFrame().readableBytes();
        int bodyLength = frame.getDataFrame().readableBytes();
        //17 = 4 + 1 + 4 + 4 + 4
        out.ensureWritable(17 + headerLength + bodyLength);
        // inner header
        //13 = 1 + 4 + 4 + 4
        out.writeInt(13 + headerLength + bodyLength);
        out.writeByte(frame.getInnerCmd());
        out.writeInt(frame.getGateSessionId());

        // raw package
        out.writeInt(4 + headerLength + bodyLength);
        out.writeInt(headerLength);
        byte[] data = new byte[headerLength];
        frame.getHeadFrame().readBytes(data);
        out.writeBytes(data);
        data = new byte[bodyLength];
        frame.getDataFrame().readBytes(data);
        out.writeBytes(data);
    }

    protected void encode(ChannelHandlerContext ctx, FrameWrapper msg, ByteBuf out) throws Exception {
        frameEncode(msg, out);
    }
}