package com.dd.game.entity.model;


import com.dd.edata.db.annotation.*;

@Table(name = "world_march")
@TablePrimaryKey(members = {"id"})
@TableIndices({@TableIndex(name = "idx_state", members = {"state"})})
public class WorldMarchModel implements IModel{

    @Column(name = "id", isNull = false)
    private long id;

    @Column(name = "player_id", isNull = false)
    private long playerId;

    @Column(name = "city_id")
    private long cityId;

    @Column(name = "team_id")
    private long teamId;

    @Column(name = "march_type")
    private byte marchType;

    @Column(name = "state")
    private byte state;

    @Column(name = "march_from")
    private int from;

    @Column(name = "march_to")
    private int dest;

    @Column(name = "target_uid")
    private long targetUid;

    @Column(name = "target_aid")
    private long targetAid;

    @Column(name = "march_time")
    private long marchTime;

    @Column(name = "start_time")
    private long startTime;

    @Column(name = "end_time")
    private long endTime;

    @Column(name = "accelerate_point")
    private int acceleratePoint;

    @Column(name = "accelerate_time")
    private long accelerateTime;

    @Column(name = "army_info")
    private byte[] armyInfo;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public byte getMarchType() {
        return marchType;
    }

    public void setMarchType(byte marchType) {
        this.marchType = marchType;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getDest() {
        return dest;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public long getTargetUid() {
        return targetUid;
    }

    public void setTargetUid(long targetUid) {
        this.targetUid = targetUid;
    }

    public long getTargetAid() {
        return targetAid;
    }

    public void setTargetAid(long targetAid) {
        this.targetAid = targetAid;
    }

    public long getMarchTime() {
        return marchTime;
    }

    public void setMarchTime(long marchTime) {
        this.marchTime = marchTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getAcceleratePoint() {
        return acceleratePoint;
    }

    public void setAcceleratePoint(int acceleratePoint) {
        this.acceleratePoint = acceleratePoint;
    }

    public long getAccelerateTime() {
        return accelerateTime;
    }

    public void setAccelerateTime(long accelerateTime) {
        this.accelerateTime = accelerateTime;
    }

    public byte[] getArmyInfo() {
        return armyInfo;
    }

    public void setArmyInfo(byte[] armyInfo) {
        this.armyInfo = armyInfo;
    }

    @Override
    public String toString() {
        return "{" + "id=" + id + ", playerId=" + playerId + ", cityId=" + cityId + ", teamId=" + teamId + ", marchType=" + marchType + ", state=" + state + ", from=" + from + ", dest=" + dest + ", targetUid=" + targetUid + ", targetAid=" + targetAid + ", marchTime=" + marchTime + ", startTime=" + startTime + ", endTime=" + endTime + '}';
    }
}