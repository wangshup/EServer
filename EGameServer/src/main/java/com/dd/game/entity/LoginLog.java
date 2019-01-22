package com.dd.game.entity;

import com.dd.game.entity.model.LoginLogModel;
import com.dd.game.utils.Constants;
import com.dd.server.utils.IdWorker;
import com.google.protobuf.Message;

public class LoginLog extends BaseEntity {

    public LoginLog(LoginLogModel model) {
        super(model);
    }

    public LoginLog(long playerId, String deviceId, String ip, int level, String gaid, String country, String platform, String appVer) {
        super(new LoginLogModel());
        setId(IdWorker.nextId(Constants.SERVER_ID));
        setPlayerId(playerId);
        setDeviceId(deviceId);
        setIp(ip);
        setLevel(level);
        setGaid(gaid);
        setCountry(country);
        setPlatform(platform);
        setAppVer(appVer);
        setLoginTime(System.currentTimeMillis());
    }

    @Override
    public Message toProtoBuf(int op) {
        return null;
    }

    public long getPlayerId() {
        return ((LoginLogModel) model).getPlayerId();
    }

    public void setPlayerId(long playerId) {
        ((LoginLogModel) model).setPlayerId(playerId);
    }

    public long getLoginTime() {
        return ((LoginLogModel) model).getLoginTime();
    }

    public void setLoginTime(long loginTime) {
        ((LoginLogModel) model).setLoginTime(loginTime);
    }

    public long getLogoutTime() {
        return ((LoginLogModel) model).getLogoutTime();
    }

    public void setLogoutTime(long logoutTime) {
        ((LoginLogModel) model).setLogoutTime(logoutTime);
    }

    public String getIp() {
        return ((LoginLogModel) model).getIp();
    }

    public void setIp(String ip) {
        ((LoginLogModel) model).setIp(ip);
    }

    public int getLevel() {
        return ((LoginLogModel) model).getLevel();
    }

    public void setLevel(int level) {
        ((LoginLogModel) model).setLevel(level);
    }

    public int getLogoutLevel() {
        return ((LoginLogModel) model).getLogoutLevel();
    }

    public void setLogoutLevel(int logoutLevel) {
        ((LoginLogModel) model).setLogoutLevel(logoutLevel);
    }

    public String getDeviceId() {
        return ((LoginLogModel) model).getDeviceId();
    }

    public void setDeviceId(String deviceId) {
        ((LoginLogModel) model).setDeviceId(deviceId);
    }

    public String getGaid() {
        return ((LoginLogModel) model).getGaid();
    }

    public void setGaid(String gaid) {
        ((LoginLogModel) model).setGaid(gaid);
    }

    public String getCountry() {
        return ((LoginLogModel) model).getCountry();
    }

    public void setCountry(String country) {
        ((LoginLogModel) model).setCountry(country);
    }

    public String getPlatform() {
        return ((LoginLogModel) model).getPlatform();
    }

    public void setPlatform(String platform) {
        ((LoginLogModel) model).setPlatform(platform);
    }

    public String getAppVer() {
        return ((LoginLogModel) model).getAppVer();
    }

    public void setAppVer(String appVer) {
        ((LoginLogModel) model).setAppVer(appVer);
    }

    public long getLogoutDiamond() {
        return ((LoginLogModel) model).getLogoutDiamond();
    }

    public void setLogoutDiamond(long logoutDiamond) {
        ((LoginLogModel) model).setLogoutDiamond(logoutDiamond);
    }

    public int getLogoutPower() {
        return ((LoginLogModel) model).getLogoutPower();
    }

    public void setLogoutPower(int logoutPower) {
        ((LoginLogModel) model).setLogoutPower(logoutPower);
    }

    public int getLogoutChapter() {
        return ((LoginLogModel) model).getLogoutChapter();
    }

    public void setLogoutChapter(int logoutChapter) {
        ((LoginLogModel) model).setLogoutChapter(logoutChapter);
    }

    public int getLogoutBattle() {
        return ((LoginLogModel) model).getLogoutBattle();
    }

    public void setLogoutBattle(int logoutBattle) {
        ((LoginLogModel) model).setLogoutBattle(logoutBattle);
    }

    public long getId() {
        return model.getId();
    }

    public void setId(long id) {
        model.setId(id);
    }
}
