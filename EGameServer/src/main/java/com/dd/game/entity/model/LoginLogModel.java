package com.dd.game.entity.model;

import com.dd.edata.db.annotation.*;

@Table(name = "login_log", policy = Table.POLICY_YEAR_MONTH)
@TablePrimaryKey(members = {"id"})
@TableIndices({@TableIndex(name = "idx_playerid", members = {"playerId"})})
public class LoginLogModel extends BaseModel {
    @Column
    private long playerId;

    @Column
    private long loginTime;

    @Column
    private long logoutTime;

    @Column(type = "char", len = 24)
    private String ip;

    @Column
    private int level;

    @Column
    private int logoutLevel;

    @Column(type = "varchar", len = 128)
    private String deviceId;

    @Column(type = "varchar", len = 36)
    private String gaid;

    @Column(type = "char", len = 10)
    private String country;

    @Column(type = "varchar", len = 36)
    private String platform;

    @Column(type = "varchar", len = 36)
    private String appVer;

    @Column
    private long logoutDiamond;

    @Column
    private int logoutPower;

    @Column
    private int logoutChapter;

    @Column
    private int logoutBattle;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public long getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(long logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLogoutLevel() {
        return logoutLevel;
    }

    public void setLogoutLevel(int logoutLevel) {
        this.logoutLevel = logoutLevel;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getGaid() {
        return gaid;
    }

    public void setGaid(String gaid) {
        this.gaid = gaid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAppVer() {
        return appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

    public long getLogoutDiamond() {
        return logoutDiamond;
    }

    public void setLogoutDiamond(long logoutDiamond) {
        this.logoutDiamond = logoutDiamond;
    }

    public int getLogoutPower() {
        return logoutPower;
    }

    public void setLogoutPower(int logoutPower) {
        this.logoutPower = logoutPower;
    }

    public int getLogoutChapter() {
        return logoutChapter;
    }

    public void setLogoutChapter(int logoutChapter) {
        this.logoutChapter = logoutChapter;
    }

    public int getLogoutBattle() {
        return logoutBattle;
    }

    public void setLogoutBattle(int logoutBattle) {
        this.logoutBattle = logoutBattle;
    }

    @Override
    public String toString() {
        return "LoginLogModel{" + "playerId=" + playerId + ", loginTime=" + loginTime + ", logoutTime=" + logoutTime + ", ip='" + ip + '\'' + ", level=" + level + ", logoutLevel=" + logoutLevel + ", deviceId='" + deviceId + '\'' + ", gaid='" + gaid + '\'' + ", country='" + country + '\'' + ", platform='" + platform + '\'' + ", appVer='" + appVer + '\'' + ", logoutDiamond=" + logoutDiamond + ", logoutPower=" + logoutPower + ", logoutChapter=" + logoutChapter + ", logoutBattle=" + logoutBattle + '}';
    }
}
