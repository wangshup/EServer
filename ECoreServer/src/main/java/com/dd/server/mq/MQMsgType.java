package com.dd.server.mq;

public enum MQMsgType {
    SendChatMsg(100), PullChatMsg(101), AllianceInfo(102);

    private int msgId;

    MQMsgType(int msgId) {
        this.msgId = msgId;
    }

    public int getMsgId() {
        return this.msgId;
    }
}
