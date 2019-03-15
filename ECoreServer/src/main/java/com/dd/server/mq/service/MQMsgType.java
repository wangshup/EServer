package com.dd.server.mq.service;

public enum MQMsgType {
    SendChatMsg(100), PullChatMsg(101), AllianceInfo(102);

    private short msgId;

    MQMsgType(int msgId) {
        this.msgId = (short) msgId;
    }

    public short getMsgId() {
        return this.msgId;
    }

    public static MQMsgType valueOf(short msgId) {
        for (MQMsgType type : values()) {
            if (type.getMsgId() == msgId) return type;
        }
        return null;
    }
}
