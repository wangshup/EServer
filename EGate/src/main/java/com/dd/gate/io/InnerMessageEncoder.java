package com.dd.gate.io;

import com.dd.gate.entities.InnerMessage;
import com.dd.protobuf.CommonProtocol;
import com.dd.server.io.FrameWrapper;
import com.dd.server.utils.BytesHolder;
import com.dd.server.utils.BytesHolder.CompositeByte;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class InnerMessageEncoder extends MessageToMessageEncoder<InnerMessage> {
    private static final BytesHolder headByteHolder = new BytesHolder();
    private static final BytesHolder bodyByteHolder = new BytesHolder();

    public static FrameWrapper responseEncode(InnerMessage msg) {
        CommonProtocol.PacketHead head = msg.getHead();
        CompositeByte headBytes = headByteHolder.toByteArray(head);
        ByteBuf headBuffer = Unpooled.wrappedBuffer(headBytes.data, 0, headBytes.length);
        CompositeByte bodyBytes = msg.getBody() != null ? bodyByteHolder.toByteArray(msg.getBody()) : bodyByteHolder.getCompositeByte(0);
        ByteBuf dataBuffer = Unpooled.wrappedBuffer(bodyBytes.data, 0, bodyBytes.length);
        FrameWrapper wrapper = new FrameWrapper();
        wrapper.setHeadFrame(headBuffer);
        wrapper.setDataFrame(dataBuffer);
        wrapper.setInnerCmd(msg.getInnerCmd());
        wrapper.setGateSessionId(msg.getSessionId());
        return wrapper;
    }

    protected void encode(ChannelHandlerContext ctx, InnerMessage msg, List<Object> out) throws Exception {
        out.add(responseEncode(msg));
    }
}