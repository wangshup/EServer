package com.dd.server.mq;

/**
 * 消息处理接口
 *
 * @author wangsp，2019.02.18
 */
@FunctionalInterface
public interface IMQMsgHandler {
    /**
     * MQ消息处理接口
     *
     * @param sequence 消息唯一序列号
     * @param srcSid   源服务器ID
     * @param error    错误码
     * @param msg      消息体
     * @return 需要response的消息，如果不需要返回，直接return null
     * @throws Exception 如果有错误要response，请throw MqException
     */
    byte[] handle(long sequence, int srcSid, int error, byte[] msg) throws Exception;
}
