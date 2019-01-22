package com.dd.gate.model;

import com.dd.edata.db.annotation.*;

@Table(name = "user")
@TablePrimaryKey(members = {"id"})
@TableIndices({@TableIndex(name = "idx_deviceid", members = {"device_id"})})
public class UserModel extends BaseModel {
    @Column(name = "device_id", isNull = false, type = "varchar", len = 128)
    private String deviceId;

    @Column(name = "server_id")
    private int serverId;

    @Column(name = "create_time")
    private long createTime;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}