package com.dd.server.gateclient;

import com.dd.server.io.FrameWrapper;
import com.dd.server.io.ServerMessageEncoder;
import com.dd.server.request.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ClientFrameEncoder extends MessageToByteEncoder<Response> {

    public static void frameEncode(Response msg, ByteBuf out) {
        FrameWrapper frame = ServerMessageEncoder.responseEncode(msg);
        int headerLength = frame.getHeadFrame().readableBytes();
        int bodyLength = frame.getDataFrame().readableBytes();
        //17 = 4 + 1 + 4 + 4 + 4
        out.ensureWritable(17 + headerLength + bodyLength);
        // inner header
        //13 = 1 + 4 + 4 + 4
        out.writeInt(13 + headerLength + bodyLength);
        out.writeByte(msg.getInnerCmd());
        out.writeInt(msg.getGateSessionId());

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

    protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) throws Exception {
        frameEncode(msg, out);
    }
}