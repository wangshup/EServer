package com.dd.game.entity.model;

import com.dd.edata.db.annotation.*;
import com.dd.game.core.GameEngine;
import com.dd.game.utils.Constants;
import com.dd.server.utils.IdWorker;

import java.util.Calendar;
import java.util.Date;

@Table(name = "user_money_log", policy = Table.POLICY_YEAR_MONTH)
@TablePrimaryKey(members = {"id"})
@TableIndices({@TableIndex(name = "idx_playerid", members = {"playerId"})})
public class MoneyLogModel extends BaseModel {
    @Column
    private long playerId;

    @Column
    private int type;

    @Column
    private long oldValue;

    @Column
    private long addValue;

    @Column
    private long newValue;

    @Column
    private Date logTime;

    @Column
    private String comment;

    public static void addLog(long playerId, int type, long oldValue, long addValue, long newValue, String comment) {
        MoneyLogModel log = new MoneyLogModel();
        log.setId(IdWorker.nextId(Constants.SERVER_ID));
        log.setPlayerId(playerId);
        log.setType(type);
        log.setOldValue(oldValue);
        log.setAddValue(addValue);
        log.setNewValue(newValue);
        log.setComment(comment);
        log.setLogTime(Calendar.getInstance().getTime());
        GameEngine.getEData().insertAsync(log);
        //log.insert(true);
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getOldValue() {
        return oldValue;
    }

    public void setOldValue(long oldValue) {
        this.oldValue = oldValue;
    }

    public long getAddValue() {
        return addValue;
    }

    public void setAddValue(long addValue) {
        this.addValue = addValue;
    }

    public long getNewValue() {
        return newValue;
    }

    public void setNewValue(long newValue) {
        this.newValue = newValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    @Override
    public String toString() {
        return "MoneyLogModel{" + "playerId=" + playerId + ", type=" + type + ", oldValue=" + oldValue + ", addValue=" + addValue + ", newValue=" + newValue + ", logTime=" + logTime + ", comment='" + comment + '\'' + '}';
    }
}
