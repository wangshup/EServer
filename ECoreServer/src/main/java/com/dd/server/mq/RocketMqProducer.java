package com.dd.server.mq;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocketMqProducer extends AbstractProducer implements IProducer {
    private static final Logger logger = LoggerFactory.getLogger(RocketMqProducer.class);
    private DefaultMQProducer producer;

    public RocketMqProducer(String topic, String mqServers) {
        super(topic, mqServers);
    }

    public void send(MQMsg msg, boolean isAsync) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        try {
            MQMsgFrame.frameEncode(MQMsgFrame.responseEncode(msg), buf);
            send(getTopic(), msg.getDstSid(), msg.getMsgId(), array(buf), isAsync);
        } catch (Exception e) {
            logger.error("send msg {} error!!!", msg, e);
        } finally {
            buf.release();
        }
    }

    private void send(String topic, int destSid, long id, byte[] msg, boolean isAsync) throws Exception {
        Message message = new Message(topic, String.valueOf(destSid), String.valueOf(id), msg);
        if (isAsync) {
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                }

                @Override
                public void onException(Throwable e) {
                    logger.error("send msg {} error!!!", message, e);
                }
            });
        } else {
            producer.send(message);
        }
    }

    @Override
    public void start() throws Exception {
        producer = new DefaultMQProducer(getTopic());
        // Specify name server addresses.
        producer.setNamesrvAddr(getMqServers());
        producer.start();
    }

    @Override
    public void shutdown() {
        producer.shutdown();
    }
}


