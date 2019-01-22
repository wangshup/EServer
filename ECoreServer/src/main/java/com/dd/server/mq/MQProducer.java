package com.dd.server.mq;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.server.utils.IdWorker;

public class MQProducer {
    private static final Logger logger = LoggerFactory.getLogger(MQProducer.class);
    private final KafkaProducer<Long, byte[]> producer;

    public MQProducer(String mqServers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", mqServers);
        props.put("client.id", "Producer@" + IdWorker.nextUUID());
        props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new KafkaProducer<>(props);
    }

    public void send(String receiver, long key, byte[] msg, boolean isAsync) {
        if (isAsync) { // Send asynchronously
            producer.send(new ProducerRecord<>(receiver, key, msg),
                    new SendCallBack(System.currentTimeMillis(), key, msg));
        } else { // Send synchronously
            try {
                producer.send(new ProducerRecord<>(receiver, key, msg)).get();
                logger.debug("[producer]:Sent message: (key:{},msg:{})", key, msg);
            } catch (InterruptedException | ExecutionException e) {
                logger.error("[producer]:Sent message: (key:{},msg:{}) error!!", key, msg, e);
            }
        }
    }
}

class SendCallBack implements Callback {
    private static final Logger logger = LoggerFactory.getLogger(SendCallBack.class);
    private final long startTime;
    private final Long key;
    private final byte[] message;

    public SendCallBack(long startTime, Long key, byte[] message) {
        this.startTime = startTime;
        this.key = key;
        this.message = message;
    }

    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {
            logger.debug("[producer]: message(" + key + ", " + message + ") sent to partition(" + metadata.partition()
                    + "), " + "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
        } else {
            exception.printStackTrace();
        }
    }
}
