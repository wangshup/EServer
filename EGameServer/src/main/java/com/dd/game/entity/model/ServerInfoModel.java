package com.dd.game.entity.model;

import com.dd.edata.db.annotation.Column;
import com.dd.edata.db.annotation.Table;
import com.dd.edata.db.annotation.TablePrimaryKey;

@Table(name = "server_info")
@TablePrimaryKey(members = {"id"})
public class ServerInfoModel extends BaseModel {

    @Column
    public volatile long nextId;
    
    @Column
    private long startTime; // 开服时间

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getNextId() {
        return nextId;
    }

    public void setNextId(long nextId) {
        this.nextId = nextId;
    }

    @Override
    public String toString() {
        return "ServerInfoModel{" + "startTime=" + startTime + ", nextId=" + nextId + '}';
    }
}
