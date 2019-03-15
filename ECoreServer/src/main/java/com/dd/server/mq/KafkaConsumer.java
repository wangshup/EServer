package com.dd.server.mq;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.concurrent.DefaultThreadFactory;
import kafka.utils.ShutdownableThread;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: Immortal
 * @description: kafka consumer
 * @author: wangshupeng
 * @create: 2019-02-20 14:51
 **/
public class KafkaConsumer extends AbstractConsumer implements IConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private org.apache.kafka.clients.consumer.KafkaConsumer<Long, byte[]> consumer;
    private ExecutorService executor;

    public KafkaConsumer(String topic, int sid, Properties prop, MQCore mqCore) {
        super(topic, sid, prop, mqCore);
    }

    @Override
    public void start() throws Exception {
        Properties props = new Properties();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "Consumer@" + getSid());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty("bootstrap.servers"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group@" + getSid());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, getProperty("auto.commit", "true"));
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, getProperty("auto.commit.interval", "1000"));
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, getProperty("session.timeout", "30000"));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(getTopic() + (getSid() & KafkaProducer.TOPIC_MASK)));
        executor = Executors.newSingleThreadExecutor(new DefaultThreadFactory("MQ-Consumer-Async-" + getSid()));
        executor.execute(new ConsumerThread());
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    private void handleRecords(ConsumerRecords<Long, byte[]> records) {
        for (ConsumerRecord<Long, byte[]> record : records) {
            logger.debug("Received async msg, key:'{}',record:'{}'", record.key(), record.value());
            if (!getMqCore().containsConsumer(record.key())) continue;
            ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
            try {
                buf.writeBytes(record.value());
                MQMsgFrame frame = MQMsgFrame.frameDecode(buf);
                getMqCore().handleMessage(new MQMsg(frame).decode());
            } catch (Exception e) {
                logger.error("decode record {} error!!!", record.key());
            } finally {
                buf.release();
            }
        }
    }

    class ConsumerThread extends ShutdownableThread {
        ConsumerThread() {
            super("KafkaConsumer", false);
        }

        @Override
        public void doWork() {
            ConsumerRecords<Long, byte[]> records = consumer.poll(Duration.ofMillis(50));
            if (records != null && !records.isEmpty()) {
                handleRecords(records);
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
}
