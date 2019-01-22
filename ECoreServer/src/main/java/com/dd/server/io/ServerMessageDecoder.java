package com.dd.server.io;

import java.util.List;

import com.dd.server.request.Request;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class ServerMessageDecoder extends MessageToMessageDecoder<FrameWrapper> {

    // 只解析 head, body 可以为空, 交由上层自己处理
    protected void decode(ChannelHandlerContext ctx, FrameWrapper msg, List<Object> out) throws Exception {
        out.add(new Request(msg));
    }
}