import com.dd.server.io.FrameWrapper;
import com.dd.server.request.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class ClientMessageDecoder extends MessageToMessageDecoder<FrameWrapper> {

    // 只解析 head, body 可以为空, 交由上层自己处理
    protected void decode(ChannelHandlerContext ctx, FrameWrapper msg, List<Object> out) throws Exception {
        out.add(new Request(msg));
    }
}