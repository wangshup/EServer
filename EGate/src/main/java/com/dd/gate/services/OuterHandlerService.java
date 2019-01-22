package com.dd.gate.services;

import com.dd.edata.db.DBWhere;
import com.dd.gate.GateServer;
import com.dd.gate.entities.InnerServer;
import com.dd.gate.entities.User;
import com.dd.gate.model.UserModel;
import com.dd.gate.session.ISession;
import com.dd.gate.session.OuterSession;
import com.dd.gate.utils.Constants;
import com.dd.protobuf.CommonProtocol.PacketHead;
import com.dd.protobuf.LoginProtocol.CSLogin;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.gateclient.ClientFrameEncoder;
import com.dd.server.io.FrameWrapper;
import com.dd.server.io.ServerFrameDecoder;
import com.dd.server.request.Request;
import com.dd.server.request.Response;
import com.dd.server.utils.ClientDisconnectionReason;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;

public class OuterHandlerService extends AbstractService {
    public OuterHandlerService() {
        super(ServiceType.OUTER_HANDLER);
    }

    // 发送给游戏服务器的数据包
    // REQUEST FRAME FORMAT
    // +----------+------------+-----------------+------------+-------------+--------+-------------+
    // |Total size| inner cmd  | gate session id | frame size | Header size | Header | Message body|
    // | 4 bytes  |  1 byte    |     4 bytes     |  4 bytes   |   4 bytes   | data   | data        |
    // +----------+------------+-----------------+------------+-------------+--------+-------------+
    //
    public void handle(ISession session, ByteBuf buf) {
        try {
            final OuterSession outerSession = ((OuterSession) session);
            int serverId = outerSession.getInnerServerId();
            if (serverId != -1) {
                InnerServer server = outerSession.getInnerServer();
                if (server != null) {
                    send2InnerServer(server, outerSession, buf);
                } else {
                    logger.error("[gate] {} handler error,server {} not found!!", session, serverId);
                }
                return;
            }

            buf.markReaderIndex();
            FrameWrapper frame = ServerFrameDecoder.frameDecode(buf);
            Request req = new Request(frame).decode();
            CSLogin login = CSLogin.newBuilder().mergeFrom(req.getBody(), 0, req.getBodyLen()).build();
            final String deviceId = login.getDeviceId();
            buf.retain();
            GateServer.getInstance().getDataService().getEData().selectAsync((userModel) -> {
                try {
                    User user;
                    if (userModel == null) {
                        InnerServer s = GateServer.getInstance().getSessionService().getInnerServer(deviceId);
                        user = new User(deviceId, s.getServerId());
                        user.insert(true);
                    } else {
                        user = new User(userModel);
                    }
                    outerSession.setInnerServerId(user.getServerId());
                    InnerServer s = outerSession.getInnerServer();
                    send2InnerServer(s, session, buf.resetReaderIndex());
                } catch (Exception e) {
                    logger.error("[gate] {} handler error!!", session, e);
                } finally {
                    buf.release();
                }
            }, session.getExecutor(), UserModel.class, DBWhere.equal("deviceId", deviceId));
        } catch (Throwable t) {
            session.getChannel().pipeline().fireExceptionCaught(t);
            logger.error("[gate] {} handler error,outer channel will be closed!!", session, t);
        } finally {
            ReferenceCountUtil.release(buf);
        }
    }

    private void send2InnerServer(InnerServer server, ISession outerSession, ByteBuf buf) {
        ByteBuf out = PooledByteBufAllocator.DEFAULT.buffer();
        // 整包长度 4字节
        // 5 = 4 + 1
        out.writeInt(buf.readableBytes() + 5);
        // 内部服务器的cmd 1字节
        out.writeByte(Constants.CMD_INNER_TUNNEL);
        // outer session id
        out.writeInt(outerSession.getSessionId());
        // 包体
        out.writeBytes(buf);
        server.send(out);
    }

    public void disconnect2InnerServer(ISession session) {
        InnerServer server = ((OuterSession) session).getInnerServer();
        if (server != null) {
            PacketHead head = PacketHead.newBuilder().setActionId(ServerEventType.DISCONNECT).setErrorCode(ClientDisconnectionReason.UNKNOWN.getValue()).build();
            ByteBuf out = PooledByteBufAllocator.DEFAULT.buffer();
            Response resp = new Response(head);
            resp.setGateSessionId(session.getSessionId());
            ClientFrameEncoder.frameEncode(resp, out);
            server.send(out);
        }
    }
}