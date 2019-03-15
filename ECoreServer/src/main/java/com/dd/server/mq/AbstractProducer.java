package com.dd.server.mq;

import io.netty.buffer.ByteBuf;

/**
 * @program: Immortal
 * @description: abstract mq producer
 * @author: wangshupeng
 * @create: 2019-02-20 15:33
 **/
public class AbstractProducer {
    private String topic;
    private String mqServers;

    public AbstractProducer(String topic, String mqServers) {
        this.topic = topic;
        this.mqServers = mqServers;
    }

    public String getTopic() {
        return topic;
    }

    public String getMqServers() {
        return mqServers;
    }

    protected byte[] array(ByteBuf buf) {
        byte[] bytes;
        int length = buf.readableBytes();
        if (buf.hasArray()) {
            bytes = buf.array();
        } else {
            bytes = new byte[length];
            buf.getBytes(buf.readerIndex(), bytes);
        }
        return bytes;
    }
}