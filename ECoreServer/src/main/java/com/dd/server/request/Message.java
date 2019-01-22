package com.dd.server.request;

import com.dd.protobuf.CommonProtocol.PacketHead;
import com.dd.server.event.param.IServerEvent;

public class Message {
    protected IServerEvent event;
    protected PacketHead head;
    protected Object body;
    protected int bodyLen;

    public Message() {
    }

    public Message(int sid, String id, com.google.protobuf.Message msg) {
        PacketHead.Builder hBuilder = PacketHead.newBuilder();
        hBuilder.setActionId(id);
        hBuilder.setSid(sid);
        this.head = hBuilder.build();
        if (msg != null) {
            this.body = msg;
        }
    }

    public Message(PacketHead head, byte[] body, int len) {
        if (head == null) {
            throw new IllegalArgumentException("PacketHead not valied!");
        }
        this.head = head;
        this.body = body;
        this.bodyLen = len;
    }

    public IServerEvent getEvent() {
        return event;
    }

    public void setEvent(IServerEvent event) {
        this.event = event;
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
