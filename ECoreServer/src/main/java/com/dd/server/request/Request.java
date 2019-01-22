package com.dd.server.request;

import com.dd.protobuf.CommonProtocol;
import com.dd.protobuf.CommonProtocol.PacketHead;
import com.dd.server.entities.IUser;
import com.dd.server.event.param.IServerEvent;
import com.dd.server.event.param.ServerEvent;
import com.dd.server.event.param.ServerEventParam;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.io.FrameWrapper;
import com.dd.server.session.ISession;
import com.dd.server.utils.BytesHolder;
import com.dd.server.utils.BytesHolder.CompositeByte;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public class Request extends Message {
    private static final BytesHolder byteHolder = new BytesHolder();
    private ISession session;
    private long receiveTime;
    private long handleStartTime;
    private String multiHandlerRequestId;
    private FrameWrapper frame;

    public Request(IServerEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event not valied!");
        }
        PacketHead.Builder hBuilder = PacketHead.newBuilder();
        hBuilder.setActionId(event.getType());
        this.head = hBuilder.build();
        this.event = event;
    }

    public Request(PacketHead head, byte[] body, int len) {
        super(head, body, len);
    }

    public Request(FrameWrapper frame) {
        this.frame = frame;
    }

    public static IServerEvent newEvent(IUser user, String eventType) {
        Map<ServerEventParam, Object> evtParams = new HashMap<ServerEventParam, Object>();
        evtParams.put(ServerEventParam.USER, user);
        evtParams.put(ServerEventParam.ZONE, user.getZone());
        return new ServerEvent(eventType, evtParams);
    }

    private static CompositeByte getBytes(ByteBuf buf) {
        int len = buf.readableBytes();
        CompositeByte bytes = byteHolder.getCompositeByte(len);
        buf.readBytes(bytes.data, 0, len);
        return bytes;
    }

    public String getMultiHandlerRequestId() {
        return multiHandlerRequestId;
    }

    public void setMultiHandlerRequestId(String multiHandlerRequestId) {
        this.multiHandlerRequestId = multiHandlerRequestId;
    }

    public boolean isGameQuest() {
        return !head.getActionId().startsWith(ServerEventType.SYSTEM_EVENT);
    }

    public long getHandleStartTime() {
        return handleStartTime;
    }

    public void setHandleStartTime(long handleStartTime) {
        this.handleStartTime = handleStartTime;
    }

    public ISession getSession() {
        return this.session;
    }

    public void setSession(ISession session) {
        this.session = session;
        setReceiveTime(System.currentTimeMillis());
    }

    public long getReceiveTime() {
        return this.receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public FrameWrapper getFrame() {
        return frame;
    }

    public Request decode() throws Exception {
        try {
            CompositeByte headBytes = getBytes(frame.getHeadFrame());
            head = CommonProtocol.PacketHead.newBuilder().mergeFrom(headBytes.data, 0, headBytes.length).build();
            CompositeByte bodyBytes = getBytes(frame.getDataFrame());
            this.body = bodyBytes.data;
            this.bodyLen = bodyBytes.length;
            return this;
        } catch (Exception e) {
            throw e;
        } finally {
            frame.release();
        }
    }

    @Override
    public String toString() {
        return "Request{" + "session=" + session + ", receiveTime=" + receiveTime + ", handleStartTime=" + handleStartTime + ", head=" + head + '}';
    }
}
