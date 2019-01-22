package com.dd.game.entity;

import com.dd.game.entity.model.ServerInfoModel;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class ServerInfo extends BaseEntity {
    private static final Logger logger = LoggerFactory.getLogger(ServerInfo.class);
    private static final AtomicLongFieldUpdater<ServerInfoModel> idUpdater;
    private static final AtomicLongFieldUpdater<ServerInfo> currentIdUpdater;

    static {
        idUpdater = AtomicLongFieldUpdater.newUpdater(ServerInfoModel.class, "nextId");
        currentIdUpdater = AtomicLongFieldUpdater.newUpdater(ServerInfo.class, "currentId");
    }

    private volatile long currentId;

    public ServerInfo(ServerInfoModel model) {
        super(model);
    }

    public long incrAndGetId() {
        while (getNextId() <= currentId) {
            incrNextId(200);
            try {
                update(false);
            } catch (Exception e) {
                logger.error("update server info error!", e);
            }
        }
        for (; ; ) {
            long id = this.currentId;
            if (currentIdUpdater.compareAndSet(this, id, id + 1)) {
                break;
            }
        }
        return this.currentId;
    }

    private long incrNextId(long incrValue) {
        for (; ; ) {
            long id = this.getNextId();
            if (idUpdater.compareAndSet((ServerInfoModel) model, id, id + incrValue)) {
                break;
            }
        }
        return this.getNextId();
    }

    @Override
    public Message toProtoBuf(int op) {
        return null;
    }

    @Override
    public String toString() {
        return "ServerInfo{" + "model=" + model + ", currentId=" + currentId + '}';
    }

    //delegate methods
    public long getStartTime() {
        return ((ServerInfoModel) model).getStartTime();
    }

    public void setStartTime(long startTime) {
        ((ServerInfoModel) model).setStartTime(startTime);
    }

    public long getNextId() {
        return ((ServerInfoModel) model).getNextId();
    }

    public void setNextId(long nextId) {
        ((ServerInfoModel) model).setNextId(nextId);
        this.currentId = this.getNextId();
    }

    public long getId() {
        return model.getId();
    }

    public void setId(long id) {
        model.setId(id);
    }
}
