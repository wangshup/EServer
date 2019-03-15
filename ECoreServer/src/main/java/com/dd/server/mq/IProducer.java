package com.dd.server.mq;

/**
 * @program: Immortal
 * @description: mq producer
 * @author: wangshupeng
 * @create: 2019-02-20 14:20
 **/
public interface IProducer {
    void start() throws Exception;

    void shutdown();

    void send(MQMsg msg, boolean isAsync);

    static IProducer createProducer(String type, String topic, String mqServers) {
        switch (type.toLowerCase()) {
            case "kafka":
                return new KafkaProducer(topic, mqServers);
            case "rocketmq":
                return new RocketMqProducer(topic, mqServers);
            default:
                break;
        }
        return null;
    }
}
