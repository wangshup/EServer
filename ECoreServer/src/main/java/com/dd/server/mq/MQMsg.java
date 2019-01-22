package com.dd.server.mq;

import com.dd.server.mq.MqProto.MsgHead;
import com.dd.server.utils.BytesHolder;
import com.dd.server.utils.IdWorker;
import com.dd.server.utils.BytesHolder.CompositeByte;

import io.netty.buffer.ByteBuf;

public class MQMsg {
    private static final BytesHolder byteHolder = new BytesHolder();
    private MQMsgFrame frame;
    protected MsgHead head;
    protected Object body;
    protected int bodyLen;

    public MQMsg() {
    }

    public MQMsg(MQMsgFrame frame) {
        this.frame = frame;
    }

    public MQMsg(long id, int srcSid, int dstSid, long uid, MQMsgType msgType, com.google.protobuf.Message msg) {
        MsgHead.Builder hBuilder = MsgHead.newBuilder();
        hBuilder.setCmdId(msgType.getMsgId());
        hBuilder.setSrcSid(srcSid);
        hBuilder.setDstSid(dstSid);
        hBuilder.setUid(uid);
        hBuilder.setId(id);
        this.head = hBuilder.build();
        if (msg != null) {
            this.body = msg;
        }
    }

    public MQMsg(int srcSid, int dstSid, long uid, MQMsgType msgType, com.google.protobuf.Message msg) {
        this(IdWorker.nextId(srcSid), srcSid, dstSid, uid, msgType, msg);
    }

    public MQMsg(MsgHead reqHead, MQMsgType msgType, com.google.protobuf.Message msg) {
        this(reqHead.getId(), reqHead.getDstSid(), reqHead.getSrcSid(), reqHead.getUid(), msgType, msg);
    }

    public int getId() {
        return head.getCmdId();
    }

    public MsgHead getHead() {
        return head;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBody() {
        return (T) body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public int getBodyLen() {
        return bodyLen;
    }

    public void setBodyLen(int bodyLen) {
        this.bodyLen = bodyLen;
    }

    public MQMsg decode() throws Exception {
        CompositeByte headBytes = getBytes(frame.getHeadFrame());
        head = MsgHead.newBuilder().mergeFrom(headBytes.data, 0, headBytes.length).build();
        CompositeByte bodyBytes = getBytes(frame.getDataFrame());
        this.body = bodyBytes.data;
        this.bodyLen = bodyBytes.length;
        return this;
    }

    private static CompositeByte getBytes(ByteBuf buf) {
        int len = buf.readableBytes();
        CompositeByte bytes = byteHolder.getCompositeByte(len);
        buf.readBytes(bytes.data, 0, len);
        return bytes;
    }

    public MQMsg retain() {
        if (frame != null) {
            frame.retain();
        }
        return this;
    }

    public void release() {
        if (frame != null) {
            frame.release();
        }
    }
}
