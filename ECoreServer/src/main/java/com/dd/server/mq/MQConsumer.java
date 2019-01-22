package com.dd.server.mq;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import kafka.utils.ShutdownableThread;

public class MQConsumer extends ShutdownableThread {
    private final KafkaConsumer<Long, byte[]> consumer;
    private final String topic;
    private final MQService mqService;

    public MQConsumer(String topic, int sid, Properties prop, MQService mqService) {
        super("KafkaConsumer", false);
        this.topic = topic;
        this.mqService = mqService;
        Properties props = new Properties();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "Consumer@" + sid);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, prop.getProperty("mq.bootstrap.servers"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group@" + sid);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, prop.getProperty("mq.auto.commit", "true"));
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, prop.getProperty("mq.auto.commit.interval", "1000"));
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, prop.getProperty("mq.session.timeout", "30000"));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.LongDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(this.topic));
    }

    @Override
    public void doWork() {
        ConsumerRecords<Long, byte[]> records = consumer.poll(50);
        if (records != null && !records.isEmpty()) {
            mqService.handleRecords(records);
        }
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }
}
