package com.dd.server.mq.service;

import com.dd.server.exceptions.ServiceInitException;
import com.dd.server.exceptions.ServiceStartException;
import com.dd.server.exceptions.ServiceStopException;
import com.dd.server.mq.IMQMsgHandler;
import com.dd.server.mq.MQCore;
import com.dd.server.services.AbstractService;
import com.dd.server.services.ServiceType;

import java.util.concurrent.ExecutorService;

/**
 * @program: server
 * @description: 消息队列服务
 * @author: wangshupeng
 * @create: 2018-11-13 16:42
 **/
public class MQService extends AbstractService {

    private MQCore mq;

    public MQService() {
        super(ServiceType.MQ);
    }

    @Override
    protected void initService() throws ServiceInitException {
        try {
            mq = new MQCore(getConfigProperties("config/mq.properties"));
        } catch (Exception e) {
            throw new ServiceInitException(e.getMessage(), e.getCause());
        }
    }

    @Override
    protected void startService() throws ServiceStartException {

    }

    @Override
    protected void stopService() throws ServiceStopException {
        mq.shutdown();
    }

    public void registerConsumer(int sid) throws Exception {
        mq.registerConsumer(sid);
    }

    /**
     * 注册消息回调处理
     *
     * @param srcSid  源服务器ID
     * @param msgType 消息类型
     * @param handler 回调处理
     */
    public void registerMsgHandler(int srcSid, MQMsgType msgType, IMQMsgHandler handler) {
        mq.registerMsgHandler(srcSid, msgType.getMsgId(), handler);
    }

    /**
     * 同步发送消息
     *
     * @param srcSid  源服务器ID
     * @param dstSid  目的服务器ID
     * @param msgType 消息类型
     * @param msg     消息体
     */
    public void sendSync(int srcSid, int dstSid, MQMsgType msgType, byte[] msg) {
        mq.sendSync(srcSid, dstSid, msgType.getMsgId(), msg);
    }

    /**
     * 异步发送消息
     *
     * @param srcSid  源服务器ID
     * @param dstSid  目的服务器ID
     * @param msgType 消息类型
     * @param msg     消息体
     */
    public void send(int srcSid, int dstSid, MQMsgType msgType, byte[] msg) {
        mq.send(srcSid, dstSid, msgType.getMsgId(), msg);
    }

    /**
     * 异步发送消息，并注册消息返回回调
     *
     * @param srcSid  源服务器ID
     * @param dstSid  目的服务器ID
     * @param msgType 消息类型
     * @param msg     消息体
     * @param handler 消息返回回调处理
     */
    public void call(int srcSid, int dstSid, MQMsgType msgType, byte[] msg, IMQMsgHandler handler) {
        mq.call(srcSid, dstSid, msgType.getMsgId(), msg, handler);
    }

    /**
     * 异步发送消息，并注册消息返回回调
     *
     * @param srcSid       源服务器ID
     * @param dstSid       目的服务器ID
     * @param msgType      消息类型
     * @param msg          消息体
     * @param handler      消息返回回调处理
     * @param callExecutor 消息返回回调处理线程
     */
    public void call(int srcSid, int dstSid, MQMsgType msgType, byte[] msg, IMQMsgHandler handler, ExecutorService callExecutor) {
        mq.call(srcSid, dstSid, msgType.getMsgId(), msg, handler, callExecutor);
    }

    /**
     * 同步发送消息，并同步等待返回（类似RPC）
     *
     * @param srcSid  源服务器ID
     * @param dstSid  目的服务器ID
     * @param msgType 消息类型
     * @param msg     消息体
     * @return 消息返回
     * @throws Exception
     */
    public byte[] call(int srcSid, int dstSid, MQMsgType msgType, byte[] msg) throws Exception {
        return mq.call(srcSid, dstSid, msgType.getMsgId(), msg);
    }

    /**
     * 同步发送消息，并同步等待返回（类似RPC）
     *
     * @param srcSid   源服务器ID
     * @param dstSid   目的服务器ID
     * @param msgType  消息类型
     * @param msg      消息体
     * @param waitTime 等待消息返回超时时间（毫秒）
     * @return 消息返回
     * @throws Exception
     */
    public byte[] call(int srcSid, int dstSid, MQMsgType msgType, byte[] msg, long waitTime) throws Exception {
        return mq.call(srcSid, dstSid, msgType.getMsgId(), msg, waitTime);
    }
}