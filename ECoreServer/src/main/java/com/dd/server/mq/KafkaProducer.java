package com.dd.server.mq;

import com.dd.server.utils.IdWorker;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaProducer extends AbstractProducer implements IProducer {
    public static final int TOPIC_MASK = 64 - 1;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private org.apache.kafka.clients.producer.KafkaProducer producer;

    public KafkaProducer(String topic, String mqServers) {
        super(topic, mqServers);
    }

    public void send(MQMsg msg, boolean isAsync) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        try {
            MQMsgFrame.frameEncode(MQMsgFrame.responseEncode(msg), buf);
            send(getTopic() + (msg.getDstSid() & TOPIC_MASK), msg.getDstSid(), array(buf), isAsync);
        } catch (Exception e) {
            logger.error("send msg {} error!!!", msg, e);
        } finally {
            buf.release();
        }
    }

    private void send(String receiver, long key, byte[] msg, boolean isAsync) throws Exception {
        if (isAsync) {
            producer.send(new ProducerRecord<>(receiver, key, msg), (metadata, exception) -> {
                if (metadata == null) {
                    logger.error("send to {} msg error!!!", receiver, exception);
                }
            });
        } else {
            producer.send(new ProducerRecord<>(receiver, key, msg)).get();
        }
    }

    @Override
    public void start() {
        Properties props = new Properties();
        props.put("bootstrap.servers", getMqServers());
        props.put("client.id", "Producer@" + IdWorker.nextUUID());
        props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new org.apache.kafka.clients.producer.KafkaProducer(props);
    }

    @Override
    public void shutdown() {
    }
}


