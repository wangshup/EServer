/**
 * 客户端
 */

import com.dd.game.utils.RandomUtil;
import com.dd.protobuf.HeartBeatProtocol;
import com.dd.protobuf.LoginProtocol;
import com.dd.server.io.FrameWrapper;
import com.dd.server.request.Request;
import com.dd.server.request.Response;
import com.dd.server.udp.Kcp;
import com.dd.server.udp.KcpClient;
import com.dd.server.udp.KcpOnUdp;
import com.google.protobuf.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ResourceLeakDetector;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;

public class TestClient extends KcpClient implements IRequestHandler {

    private static void udpTest(TestClient tc, String ip, int port, int sessionId) {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        tc.noDelay(1, 20, 2, 1);
        tc.setMinRto(10);
        tc.wndSize(32, 32);
        tc.setTimeout(10 * 1000);
        tc.setMtu(512);
        tc.connect(new InetSocketAddress(ip, port));
        tc.start();
        tc.setConv(RandomUtil.random(100000));
        tc.setSessionId(sessionId);
        HeartBeatProtocol.CSHeartBeat.Builder b = HeartBeatProtocol.CSHeartBeat.newBuilder().setClientTime(String.valueOf(System.currentTimeMillis()));
        Response resp = new Response("system.pingpong", b.build());
        ByteBuf out = PooledByteBufAllocator.DEFAULT.buffer();
        ClientFrameEncoder.frameEncode(ClientMessageEncoder.responseEncode(resp), out);
        tc.send(out);
    }

    private static void connectAndLogin(String host, int port, Message login) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast("FrameDecoder", new ClientFrameDecoder(1000000, 0));
                    p.addLast("MessageDecoder", new ClientMessageDecoder());
                    p.addLast("FrameEncoder", new ClientFrameEncoder());
                    p.addLast("MessageEncoder", new ClientMessageEncoder());
                    p.addLast("ServerHandler", new ClientChannelHandler(new TestClient(), login));
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        //udpTest("localhost", 9033, 1, 1);
        LoginProtocol.CSLogin.Builder builder = LoginProtocol.CSLogin.newBuilder();
        builder.setDeviceId("67B917EB-92DE-48FD-BB7F-E882AC7169BE");
        builder.setAppVer("1.18.0");
        builder.setCountry("CN");
        builder.setPlatform("Windows");
        builder.setProtoVer(4);
        builder.setZoneName("RPG_1");
        connectAndLogin("192.168.1.30", 8088, builder.build());
    }

    @Override
    public void handleReceive(ByteBuf bb, KcpOnUdp kcp) {
        try {
            FrameWrapper f = ClientFrameDecoder.frameDecode(bb, ByteOrder.BIG_ENDIAN);
            Request r = new Request(f);
            r.decode();
            System.out.println("udp==============" + r);
            HeartBeatProtocol.CSHeartBeat.Builder b = HeartBeatProtocol.CSHeartBeat.newBuilder().setClientTime(String.valueOf(System.currentTimeMillis()));
            Response resp = new Response("system.pingpong", b.build());
            ByteBuf out = PooledByteBufAllocator.DEFAULT.buffer();
            ClientFrameEncoder.frameEncode(ClientMessageEncoder.responseEncode(resp), out);
            send(out);
        } catch (Exception e) {
        } finally {
            bb.release();
        }
    }

    /**
     * kcp异常，之后此kcp就会被关闭
     *
     * @param ex
     * @param kcp
     */
    @Override
    public void handleException(Throwable ex, KcpOnUdp kcp) {
        System.out.println(ex);
    }

    @Override
    public void handleClose(KcpOnUdp kcp) {
        super.handleClose(kcp);
        System.out.println("服务器离开:" + kcp);
        System.out.println("waitSnd:" + kcp.getKcp().waitSnd());
    }

    @Override
    public void out(ByteBuf msg, Kcp kcp, Object user) {
        super.out(msg, kcp, user);
    }

    @Override
    public void handle(Channel ch, Request request) throws Exception {
        request.decode();
        if ("system.login".equals(request.getHead().getActionId())) {
            LoginProtocol.SCLogin.Builder login = LoginProtocol.SCLogin.newBuilder().mergeFrom(request.getBody(), 0, request.getBodyLen());
            udpTest(this, login.getUdpIp(), login.getUdpPort(), login.getSessionId());
        } else {
            System.out.println("tcp**************" + request);
            HeartBeatProtocol.CSHeartBeat.Builder b = HeartBeatProtocol.CSHeartBeat.newBuilder().setClientTime(String.valueOf(System.currentTimeMillis()));
            Response resp = new Response("system.pingpong", b.build());
            ByteBuf out = PooledByteBufAllocator.DEFAULT.buffer();
            ClientFrameEncoder.frameEncode(ClientMessageEncoder.responseEncode(resp), out);
            send(out);
        }
    }
}
