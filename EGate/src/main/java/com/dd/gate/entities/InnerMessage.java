package com.dd.gate.entities;

import com.dd.protobuf.CommonProtocol.PacketHead;
import com.google.protobuf.Message;

public class InnerMessage {
    private byte innerCmd;
    private int sessionId;
    private PacketHead head;
    private Object body;
    private int bodyLen;

    public InnerMessage() {
    }

    public InnerMessage(int sid, String id, Message msg) {
        PacketHead.Builder hBuilder = PacketHead.newBuilder();
        hBuilder.setActionId(id);
        hBuilder.setSid(sid);
        this.head = hBuilder.build();
        if (msg != null) {
            this.body = msg;
        }
    }

    public byte getInnerCmd() {
        return innerCmd;
    }

    public void setInnerCmd(byte innerCmd) {
        this.innerCmd = innerCmd;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getId() {
        return head.getActionId();
    }

    public PacketHead getHead() {
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
}
