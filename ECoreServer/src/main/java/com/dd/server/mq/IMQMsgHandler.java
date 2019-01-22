
package com.dd.server.mq;

/**
 * 消息处理接口
 * 
 * @author wangsp，2017.08.07
 *
 */
@FunctionalInterface
public interface IMQMsgHandler {
    void handler(MQMsg msg) throws Exception;
}
