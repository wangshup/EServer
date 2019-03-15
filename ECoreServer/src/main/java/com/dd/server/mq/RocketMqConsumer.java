package com.dd.server.mq;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @program: Immortal
 * @description: rocketmq consumer
 * @author: wangshupeng
 * @create: 2019-02-20 14:51
 **/
public class RocketMqConsumer extends AbstractConsumer implements IConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RocketMqConsumer.class);
    DefaultMQPushConsumer consumer;

    public RocketMqConsumer(String topic, int sid, Properties prop, MQCore mqService) {
        super(topic, sid, prop, mqService);
    }

    @Override
    public void start() throws Exception {
        // Instantiate with specified consumer group name.
        consumer = new DefaultMQPushConsumer(String.valueOf(getSid()));

        // Specify name server addresses.
        consumer.setNamesrvAddr(getProperty("bootstrap.servers"));

        // Subscribe one more more topics to consume.
        consumer.subscribe(getTopic(), String.valueOf(getSid()));
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            msgs.forEach(msg -> {
                ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
                try {
                    buf.writeBytes(msg.getBody());
                    MQMsgFrame frame = MQMsgFrame.frameDecode(buf);
                    getMqCore().handleMessage(new MQMsg(frame).decode());
                } catch (Exception e) {
                    logger.error("decode message {} error!!!", msg.getMsgId());
                } finally {
                    buf.release();
                }
            });
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        //Launch the consumer instance.
        consumer.start();
    }

    @Override
    public void shutdown() {
        consumer.shutdown();
    }
}