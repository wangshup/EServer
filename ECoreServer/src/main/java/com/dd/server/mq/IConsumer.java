package com.dd.server.mq;

import java.util.Properties;

/**
 * @program: Immortal
 * @description: mq consumer
 * @author: wangshupeng
 * @create: 2019-02-20 14:19
 **/
public interface IConsumer {
    void start() throws Exception;

    void shutdown();

    static IConsumer createConsumer(String type, int sid, String topic, Properties prop, MQCore mqCore) {
        switch (type.toLowerCase()) {
            case "kafka":
                return new KafkaConsumer(topic, sid, prop, mqCore);
            case "rocketmq":
                return new RocketMqConsumer(topic, sid, prop, mqCore);
            default:
                break;
        }
        return null;
    }
}
