package com.dd.server.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 消息队列服务类
 *
 * @author wangshupeng，2019.02.18
 */
public class MQCore {
    private static final Logger logger = LoggerFactory.getLogger(MQCore.class);
    private static final MQMsg NULL_MSG = new MQMsg();
    private Properties prop;
    private Map<Long, MsgCallBack> msgEphemeralHandlers = new ConcurrentHashMap<>();
    private Map<Long, MQMsg> syncMsgs = new ConcurrentHashMap<>();
    private Map<Integer, IMQMsgHandler> msgHandlers = new HashMap<>();
    private IProducer producer;
    private Map<Long, IConsumer> consumers = new HashMap<>();
    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();
    private MQExecutors executors = new MQExecutors(Runtime.getRuntime().availableProcessors(), "MQ-Consumer");

    public MQCore(Properties prop) throws Exception {
        this.prop = prop;
        String mqServers = prop.getProperty("bootstrap.servers");
        producer = IProducer.createProducer(prop.getProperty("type", "kafka"), prop.getProperty("topic"), mqServers);
        producer.start();
    }

    public synchronized void registerConsumer(int sid) throws Exception {
        if (consumers.containsKey((long) sid)) return;
        IConsumer consumer = IConsumer.createConsumer(prop.getProperty("type", "kafka"), sid, prop.getProperty("topic"), prop, this);
        consumer.start();
        consumers.put((long) sid, consumer);
    }

    public synchronized void registerMsgHandler(int sid, short msgId, IMQMsgHandler handler) {
        msgHandlers.put(sid << 16 | msgId, handler);
    }

    public boolean containsConsumer(long sid) {
        return consumers.containsKey(sid);
    }

    public void shutdown() {
        producer.shutdown();
        for (IConsumer consumer : consumers.values()) {
            consumer.shutdown();
        }
        executors.shutdown();
    }

    protected void handleMessage(MQMsg msg) {
        executors.getExecutor(msg.getSrcSid()).execute(() -> {
            try {
                //sync response msg
                if (syncMsgs.computeIfPresent(msg.getSequence(), (k, v) -> msg.retain()) != null) {
                    lock.lock();
                    try {
                        cond.signalAll();
                    } finally {
                        lock.unlock();
                    }
                    return;
                }

                //callback response msg
                MsgCallBack callback = msgEphemeralHandlers.remove(msg.getSequence());
                if (callback != null) {
                    callback(msg.retain(), callback.handler, callback.executor);
                    return;
                }

                //response msg
                if (msg.getMsgId() < 0) {
                    logger.warn("Response Msg [id: {}, from: {}] handler not found!", msg.getMsgId() * -1, msg.getSrcSid());
                    return;
                }

                //request msg
                IMQMsgHandler handler = msgHandlers.get(msg.getDstSid() << 16 | msg.getMsgId());
                if (handler != null) {
                    try {
                        byte[] resp = handler.handle(msg.getSequence(), msg.getSrcSid(), msg.getErrCode(), msg.getBody());
                        if (resp != null) { //response msg的ID用请求的ID的负值，以区分请求
                            send(msg.getDstSid(), msg.getSrcSid(), (short) (msg.getMsgId() * -1), msg.getSequence(), 0, resp);
                        }
                    } catch (MqException e) {
                        //response msg的ID用请求的ID的负值，以区分请求
                        byte[] error = e.getMessage() == null ? null : e.getMessage().getBytes(StandardCharsets.UTF_8);
                        send(msg.getDstSid(), msg.getSrcSid(), (short) (msg.getMsgId() * -1), msg.getSequence(), e.getError(), error);
                        throw e;
                    }
                } else {
                    logger.error("Msg [id: {}, from: {}] handler not found!", msg.getMsgId(), msg.getSrcSid());
                }
            } catch (Exception e) {
                logger.error("handle msg {} error!!", msg, e);
            } finally {
                msg.release();
            }
        });
    }

    private void send(int srcSid, int dstSid, short msgId, long sequence, int error, byte[] msg) {
        producer.send(new MQMsg(srcSid, dstSid, msgId, sequence, error, msg), true);
    }

    /**
     * 同步发送消息
     *
     * @param srcSid 源服务器ID
     * @param dstSid 目的服务器ID
     * @param msgId  消息ID
     * @param msg    消息体
     */
    public void sendSync(int srcSid, int dstSid, short msgId, byte[] msg) {
        producer.send(new MQMsg(srcSid, dstSid, msgId, 0, msg), false);
    }

    /**
     * 异步发送消息
     *
     * @param srcSid 源服务器ID
     * @param dstSid 目的服务器ID
     * @param msgId  消息ID
     * @param msg    消息体
     */
    public void send(int srcSid, int dstSid, short msgId, byte[] msg) {
        producer.send(new MQMsg(srcSid, dstSid, msgId, 0, msg), true);
    }

    /**
     * 异步发送消息，并注册消息回调
     *
     * @param srcSid  源服务器ID
     * @param dstSid  目的服务器ID
     * @param msgId   消息ID
     * @param msg     消息体
     * @param handler 回调消息处理
     */
    public void call(int srcSid, int dstSid, short msgId, byte[] msg, IMQMsgHandler handler) {
        call(srcSid, dstSid, msgId, msg, handler, null);
    }

    /**
     * 异步发送消息，并注册消息回调
     *
     * @param srcSid       源服务器ID
     * @param dstSid       目的服务器ID
     * @param msgId        消息ID
     * @param msg          消息体
     * @param handler      消息回调处理
     * @param callExecutor 消息回调处理线程
     */
    public void call(int srcSid, int dstSid, short msgId, byte[] msg, IMQMsgHandler handler, ExecutorService callExecutor) {
        MQMsg mqMsg = new MQMsg(srcSid, dstSid, msgId, 0, msg);
        if (handler != null) {
            msgEphemeralHandlers.put(mqMsg.getSequence(), new MsgCallBack(handler, callExecutor));
        }
        producer.send(mqMsg, true);
    }

    /**
     * 同步发送消息并同步等待（类似RPC)
     *
     * @param srcSid 源服务器ID
     * @param dstSid 目的服务器ID
     * @param msgId  消息ID
     * @param msg    消息体
     * @return 消息返回
     * @throws Exception
     */
    public byte[] call(int srcSid, int dstSid, short msgId, byte[] msg) throws Exception {
        return call(srcSid, dstSid, msgId, msg, 30000);
    }

    /**
     * 同步发送消息并同步等待（类似RPC)
     *
     * @param srcSid   源服务器ID
     * @param dstSid   目的服务器ID
     * @param msgId    消息ID
     * @param msg      消息体
     * @param waitTime 消息返回等待超时时间（毫秒）
     * @return 消息返回
     * @throws Exception
     */
    public byte[] call(int srcSid, int dstSid, short msgId, byte[] msg, long waitTime) throws Exception {
        // 同步发送，同步接收
        MQMsg mqMsg = new MQMsg(srcSid, dstSid, msgId, 0, msg);
        MQMsg response = NULL_MSG;
        long key = mqMsg.getSequence();
        long startTime = System.currentTimeMillis();
        lock.lock();
        try {
            syncMsgs.put(key, NULL_MSG);
            producer.send(mqMsg, false);
            while (syncMsgs.get(key) == NULL_MSG) {
                long remainTime = waitTime - (System.currentTimeMillis() - startTime);
                if (remainTime <= 0 || !cond.await(remainTime, TimeUnit.MILLISECONDS)) {
                    throw new TimeoutException();
                }
            }
            response = syncMsgs.remove(key);
            if (response != null && response.getErrCode() != 0) {
                throw new MqException(response.getErrCode());
            }
            return response.getBody();
        } finally {
            lock.unlock();
            response.release();
        }
    }

    private void callback(final MQMsg msg, IMQMsgHandler handler, ExecutorService callExecutor) {
        Runnable r = () -> {
            try {
                handler.handle(msg.getSequence(), msg.getSrcSid(), msg.getErrCode(), msg.getBody());
            } catch (Exception e) {
                logger.error("handle msg {} error!!", msg, e);
            } finally {
                msg.release();
            }
        };
        if (callExecutor != null) {
            callExecutor.execute(r);
        } else {
            r.run();
        }
    }

    public static class MsgCallBack {
        IMQMsgHandler handler;
        ExecutorService executor;

        public MsgCallBack(IMQMsgHandler handler, ExecutorService executor) {
            this.handler = handler;
            this.executor = executor;
        }
    }
}
