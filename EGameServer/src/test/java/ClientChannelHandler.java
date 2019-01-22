import com.dd.server.request.Request;
import com.dd.server.request.Response;
import com.google.protobuf.Message;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

public class ClientChannelHandler extends ChannelDuplexHandler {
    private IRequestHandler handler;
    private Message login;

    public ClientChannelHandler(IRequestHandler handler, Message login) {
        this.handler = handler;
        this.login = login;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        Response resp = new Response("system.login", login);
        channel.writeAndFlush(resp);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Request)) {
            return;
        }

        handler.handle(ctx.channel(), (Request) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}