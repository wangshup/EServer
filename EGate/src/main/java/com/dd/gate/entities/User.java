package com.dd.gate.entities;

import com.dd.gate.model.UserModel;
import com.dd.gate.utils.IdWorker;

public class User extends BaseEntity {

    public User(UserModel model) {
        super(model);
    }

    public User(String deviceId, int serverId) {
        super(new UserModel());
        setId(IdWorker.nextId(serverId));
        setDeviceId(deviceId);
        setServerId(serverId);
        setCreateTime(System.currentTimeMillis());
    }

    public String getDeviceId() {
        return ((UserModel) model).getDeviceId();
    }

    public void setDeviceId(String deviceId) {
        ((UserModel) model).setDeviceId(deviceId);
    }

    public int getServerId() {
        return ((UserModel) model).getServerId();
    }

    public void setServerId(int serverId) {
        ((UserModel) model).setServerId(serverId);
    }

    public long getCreateTime() {
        return ((UserModel) model).getCreateTime();
    }

    public void setCreateTime(long createTime) {
        ((UserModel) model).setCreateTime(createTime);
    }

    public long getId() {
        return model.getId();
    }

    public void setId(long id) {
        model.setId(id);
    }
}