package com.dd.server.mq;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.server.Server;
import com.dd.server.exceptions.ServiceInitException;
import com.dd.server.exceptions.ServiceStartException;
import com.dd.server.exceptions.ServiceStopException;
import com.dd.server.services.AbstractService;
import com.dd.server.services.ServiceType;
import com.dd.server.utils.ServerThreadFactory;
import com.google.protobuf.Parser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

/**
 * 消息队列服务类
 * 
 * @author wangshupeng，2017.12.07
 *
 */
public final class MQService extends AbstractService {
    private static final Logger logger = LoggerFactory.getLogger(MQService.class);
    private String topic;
    private Properties prop;
    private Map<Long, MsgCallBack> msgEphemeralHandlers = new ConcurrentHashMap<>();
    private Map<Long, MQMsg> syncMsgs = new ConcurrentHashMap<>();
    private Map<Integer, IMQMsgHandler> msgHandlers = new HashMap<>();
    private MQProducer producer;
    private Map<Long, ExecutorService> consumers = new HashMap<>();
    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();
    private static MQMsg nullMsg = new MQMsg();

    public MQService() {
        super(ServiceType.MQ);
    }

    @Override
    protected void initService() throws ServiceInitException {
        this.prop = getConfigProperties("config/mq.properties");
        topic = prop.getProperty("mq.topic");
    }

    @Override
    protected void startService() throws ServiceStartException {
        producer = new MQProducer(prop.getProperty("mq.bootstrap.servers"));
    }

    @Override
    protected void stopService() throws ServiceStopException {
        for (ExecutorService es : consumers.values())
            es.shutdown();
    }

    public synchronized void registerConsume(int sid) {
        if (consumers.containsKey((long) sid))
            return;
        ExecutorService executor = Executors
                .newSingleThreadExecutor(new ServerThreadFactory("MQ-Consume-Async-" + sid));
        executor.execute(new MQConsumer(topic, sid, prop, this));
        consumers.put((long) sid, executor);
    }

    public synchronized void registerMsgHandler(int sid, MQMsgType msgType, IMQMsgHandler handler) {
        msgHandlers.put(sid << 16 | msgType.getMsgId(), handler);
    }

    public void handleRecords(ConsumerRecords<Long, byte[]> records) {
        for (ConsumerRecord<Long, byte[]> record : records) {
            logger.debug("Received async msg, key:'{}',record:'{}'", record.key(), record.value());
            if (!consumers.containsKey(record.key()))
                continue;
            Server.getInstance().getExecutorService().getExecutor().execute(() -> {
                MQMsg msg = null;
                ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
                try {
                    buf.writeBytes(record.value());
                    MQMsgFrame frame = MQMsgFrame.frameDecode(buf);
                    msg = new MQMsg(frame).decode();
                    if (syncMsgs.replace(msg.getHead().getId(), msg) != null) {
                        msg.retain();
                        lock.lock();
                        try {
                            cond.signalAll();
                        } finally {
                            lock.unlock();
                        }
                    } else {
                        IMQMsgHandler handler = null;
                        MsgCallBack callback = msgEphemeralHandlers.remove(msg.getHead().getId());
                        if (callback != null) {
                            handler = callback.hanlder;
                            if (callback.executor != null) {
                                callback(msg, handler, callback.executor);
                                return;
                            }
                        } else {
                            handler = msgHandlers.get(msg.getHead().getDstSid() << 16 | msg.getHead().getCmdId());
                        }
                        if (handler != null) {
                            handler.handler(msg);
                        } else {
                            logger.error("key: {}, msg: {} handler not found!", record.key(), msg);
                        }
                    }
                } catch (Exception e) {
                    logger.error("handle record {} error!!", record, e);
                } finally {
                    buf.release();
                    if (msg != null) {
                        msg.release();
                    }
                }
            });
        }
    }

    public void send(int sid, MQMsg msg) {
        send(sid, msg, true);
    }

    private void send(int sid, MQMsg msg, boolean isAsync) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        try {
            MQMsgFrame.frameEncode(MQMsgFrame.responseEncode(msg), buf);
            producer.send(topic, msg.getHead().getDstSid(), array(buf), isAsync);
        } finally {
            buf.release();
        }
    }

    public void call(int sid, MQMsg msg, IMQMsgHandler handler) {
        call(sid, msg, handler, null);
    }

    public void call(int sid, MQMsg msg, IMQMsgHandler handler, ExecutorService callExecutor) {
        if (handler != null) {
            msgEphemeralHandlers.put(msg.getHead().getId(), new MsgCallBack(handler, callExecutor));
        }
        send(sid, msg, true);
    }

    public <T> T call(int sid, MQMsg msg, Parser<T> parser) {
        return call(sid, msg, parser, 30000);
    }

    public <T> T call(int sid, MQMsg msg, Parser<T> parser, long waitTime) {
        // 异步发送，同步接收
        MQMsg response = null;
        try {
            long key = msg.getHead().getId();
            syncMsgs.put(key, nullMsg);
            long startTime = System.currentTimeMillis();
            lock.lock();
            try {
                this.send(sid, msg, false);
                while (syncMsgs.get(key) == nullMsg) {
                    long remainTime = waitTime - (System.currentTimeMillis() - startTime);
                    if (remainTime <= 0 || !cond.await(remainTime, TimeUnit.MILLISECONDS))
                        break;
                }
                response = syncMsgs.remove(key);
                if (response == nullMsg)
                    return null;
                return (T) parser.parseFrom(response.getBody(), 0, response.getBodyLen());
            } catch (Exception e) {
                logger.debug("sync call await error", e);
            } finally {
                lock.unlock();
            }
        } finally {
            if (response != null)
                response.release();
        }
        return null;
    }

    private void callback(final MQMsg msg, IMQMsgHandler handler, ExecutorService callExecutor) {
        msg.retain();
        callExecutor.execute(() -> {
            try {
                handler.handler(msg);
            } catch (Exception e) {
                logger.error("handle msg {} error!!", msg, e);
            } finally {
                msg.release();
            }
        });
    }

    private byte[] array(ByteBuf buf) {
        byte[] bytes;
        int length = buf.readableBytes();
        if (buf.hasArray()) {
            bytes = buf.array();
        } else {
            bytes = new byte[length];
            buf.getBytes(buf.readerIndex(), bytes);
        }
        return bytes;
    }

    public static class MsgCallBack {
        IMQMsgHandler hanlder;
        ExecutorService executor;

        public MsgCallBack(IMQMsgHandler handler, ExecutorService executor) {
            this.hanlder = handler;
            this.executor = executor;
        }
    }
}
