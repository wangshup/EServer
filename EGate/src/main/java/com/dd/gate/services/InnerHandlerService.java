package com.dd.gate.services;

import com.dd.gate.GateServer;
import com.dd.gate.entities.InnerMessage;
import com.dd.gate.entities.InnerServer;
import com.dd.gate.entities.InnerServer.ServerType;
import com.dd.gate.session.ISession;
import com.dd.gate.utils.Constants;
import com.dd.protobuf.GateProtocol;
import com.dd.server.event.param.ServerEventType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.util.ReferenceCountUtil;

public class InnerHandlerService extends AbstractService {
    public InnerHandlerService() {
        super(ServiceType.INNER_HANDLER);
    }

    public void handle(ISession session, ByteBuf buf) {
        try {
            SessionService ss = GateServer.getInstance().getSessionService();
            int frameLength = buf.readInt();
            byte cmd = buf.readByte();
            int outerSessionId = buf.readInt();
            switch (cmd) {
                case Constants.CMD_INNER_REGISTER: {
                    frameLength = buf.readInt();
                    int headLen = buf.readInt();
                    byte[] body = new byte[frameLength - 4 - headLen];
                    buf.skipBytes(headLen);
                    buf.readBytes(body);
                    GateProtocol.CSInnerServerRegister isr = GateProtocol.CSInnerServerRegister.parseFrom(body);
                    InnerServer server = new InnerServer(isr.getSid(), isr.getName(), ServerType.valueOf(isr.getType()), session);
                    ss.addInnerServer(server);
                    String udpIp = GateServer.getInstance().getConfiguration().getString("udp.ip", "");
                    int udpPort = GateServer.getInstance().getConfiguration().getInt("udp.port", 10);
                    GateProtocol.SCInnerServerRegister scInner = GateProtocol.SCInnerServerRegister.newBuilder().setSid(server.getServerId()).setUdpIp(udpIp).setUdpPort(udpPort).build();
                    InnerMessage resp = new InnerMessage(server.getServerId(), ServerEventType.GATE_REGISTER, scInner);
                    resp.setInnerCmd(cmd);
                    resp.setSessionId(session.getSessionId());
                    server.send(resp);
                    break;
                }
                case Constants.CMD_INNER_TUNNEL: {
                    ISession outerSession = ss.getSession(outerSessionId);
                    if (outerSession != null) {
                        outerSession.send(buf.retainedSlice(buf.readerIndex(), frameLength - 5));
                    }
                    break;
                }
                case Constants.CMD_INNER_PINGPONG:
                    logger.debug("[gate] recv pingpong msg from {}", session);
                    break;
                case Constants.CMD_INNER_DISCONNECT:
                    ISession outerSession = ss.removeSession(outerSessionId);
                    if (outerSession != null) {
                        outerSession.closeUdp();
                        if (outerSession.getChannel() != null && outerSession.getChannel().isActive()) {
                            ChannelFuture future = outerSession.send(buf.retainedSlice(buf.readerIndex(), frameLength - 5));
                            future.addListener(f -> outerSession.getChannel().close());
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("[gate] {} handler error,channel will be closed!!", session, e);
        } finally {
            ReferenceCountUtil.safeRelease(buf);
        }
    }
}