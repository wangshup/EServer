package com.dd.server.mq;

import com.dd.server.utils.IdWorker;

class MQMsg {
    private MQMsgFrame frame;
    private MqProto.MsgHead head;
    private Object body;

    MQMsg() {
    }

    MQMsg(MQMsgFrame frame) {
        this.frame = frame;
    }

    MQMsg(int srcSid, int dstSid, short msgId, long sequence, int error, byte[] data) {
        MqProto.MsgHead.Builder hBuilder = MqProto.MsgHead.newBuilder();
        hBuilder.setMsgId(msgId);
        hBuilder.setSrcSid(srcSid);
        hBuilder.setDstSid(dstSid);
        hBuilder.setErrCode(error);
        hBuilder.setSequence(sequence);
        this.head = hBuilder.build();
        this.body = data;
    }

    MQMsg(int srcSid, int dstSid, short msgId, int error, byte[] data) {
        this(srcSid, dstSid, msgId, IdWorker.nextId(srcSid), error, data);
    }

    short getMsgId() {
        return (short) head.getMsgId();
    }

    long getSequence() {
        return head.getSequence();
    }

    int getSrcSid() {
        return head.getSrcSid();
    }

    int getDstSid() {
        return head.getDstSid();
    }

    int getErrCode() {
        return head.getErrCode();
    }

    String getErrInfo() {
        return head.getErrInfo();
    }

    public MqProto.MsgHead getHead() {
        return head;
    }

    <T> T getBody() {
        return (T) body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    MQMsg decode() throws Exception {
        byte[] headData = new byte[frame.getHeadFrame().readableBytes()];
        frame.getHeadFrame().readBytes(headData);
        head = MqProto.MsgHead.parseFrom(headData);
        this.body = new byte[frame.getDataFrame().readableBytes()];
        frame.getDataFrame().readBytes((byte[]) body);
        return this;
    }

    MQMsg retain() {
        if (frame != null) {
            frame.retain();
        }
        return this;
    }

    void release() {
        if (frame != null) {
            frame.release();
        }
    }

    @Override
    public String toString() {
        return "MQMsg{" + "head=" + head + ", body=" + body + '}';
    }
}
