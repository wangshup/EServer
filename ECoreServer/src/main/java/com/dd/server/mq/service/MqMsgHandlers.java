package com.dd.server.mq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: Immortal
 * @description: mq 消息处理类
 * @author: wangshupeng
 * @create: 2019-02-19 11:21
 **/
public final class MqMsgHandlers {
    protected static Logger logger = LoggerFactory.getLogger(MqMsgHandlers.class);

    public static byte[] handlerChatMsg(long sequence, int srcSid, int error, byte[] msg) throws Exception {
        String body = new String(msg, "UTF-8");
        logger.info("recv msg [sequence: {}, error code: {}, body: {}] from {}!!!", sequence, error, body, srcSid);
        //MQService mq = Server.getInstance().getService(ServiceType.MQ);
        //mq.send(sequence, srcSid, MQMsgType.SendChatMsg, 0, body.getBytes("UTF-8"));
        return null;
    }
}