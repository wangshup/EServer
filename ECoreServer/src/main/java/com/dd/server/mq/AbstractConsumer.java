package com.dd.server.mq;

import java.util.Properties;

/**
 * @program: Immortal
 * @description: abstract mq consumer
 * @author: wangshupeng
 * @create: 2019-02-20 15:12
 **/
public class AbstractConsumer {
    private int sid;
    private String topic;
    private MQCore mqCore;
    private Properties prop;

    public AbstractConsumer(String topic, int sid, Properties prop, MQCore mqCore) {
        this.topic = topic;
        this.sid = sid;
        this.prop = prop;
        this.mqCore = mqCore;
    }

    public int getSid() {
        return sid;
    }

    public String getTopic() {
        return topic;
    }

    public MQCore getMqCore() {
        return mqCore;
    }

    public String getProperty(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}