package com.dd.game.entity.model;

import com.dd.edata.db.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Table(name = "player")
@TablePrimaryKey(members = {"id"})
@TableIndices({@TableIndex(name = "idx_name", members = {"name"}), @TableIndex(name = "idx_account", members = {"account"}), @TableIndex(name = "idx_deviceid", members = {"deviceId"})})
public class PlayerModel extends BaseModel {

    public static final long RESERVED_MASK_DEMO_BATTLE = 0xFFL;
    public static final long RESERVED_MASK_STORY_BATTLE = 0xFF00L;
    public static final long RESERVED_MASK_GAME_SCORE = 0x030000L;

    @Column(type = "varchar", len = 64, comment = "账号")
    private String account;

    @Column(type = "varchar", len = 36, comment = "名称")
    private String name;

    @Column(type = "varchar", len = 128, comment = "设备号")
    private String deviceId;

    @Column(type = "varchar", len = 36, comment = "每次更新的gaid")
    private String gaid;

    @Column(comment = "vip等级")
    private short vip;

    @Column
    private int level;

    // 货币相关
    @Column(comment = "钻石")
    private long diamond;

    @Column(comment = "充值钻石")
    private long payDiamond;

    @Column(comment = "金币")
    private long gold;

    @Column(comment = "荣誉")
    private long honor;

    @Column(comment = "勋章")
    private long medal;

    @Column(comment = "友情点")
    private long friendship;

    @Column(comment = "体力")
    private int power;

    @Column(comment = "体力恢复时间")
    private long powerRenewTime;

    @Column(comment = "pvp体力")
    private int pvpPower;

    @Column(comment = "体力恢复时间")
    private long pvpPowerRenewTime;

    @Column(comment = "头像id")
    private int modId;

    @Column(comment = "origin sid")
    private int originSid;

    @Column(comment = "current sid")
    private int currentSid;

    @Column(comment = "创建日期")
    private Date createtime;

    @Column(comment = "封停")
    private long bannedtime;

    @Column(comment = "状态")
    private short status; // 0:正常 1:封停

    @Column(comment = "禁言时间")
    private long chatbannedtime;

    @Column(comment = "是否禁言")
    private short chatbanned; // 0:正常 1:禁言

    @Column(comment = "体力购买次数")
    private int powerBuyTimes;

    @Column(comment = "体力购买日期")
    private long powerBuyDate;

    @Column(comment = "金币购买次数")
    private int goldBuyTimes;

    @Column(comment = "金币购买日期")
    private long goldBuyDate;

    @Column(comment = "修改名称次数")
    private int nameChangedTimes;

    // 注册时
    @Column(type = "varchar", len = 128, comment = "注册设备号")
    private String deviceId0;

    @Column(type = "varchar", len = 36, comment = "首次注册时的gaid")
    private String gaid0;

    @Column(type = "char", len = 10, comment = "国家")
    private String country0;

    @Column(type = "varchar", len = 36, comment = "平台")
    private String platform0;

    @Column(type = "char", len = 24)
    private String ip0;

    @Column(type = "varchar", len = 24)
    private String appVer0;

    // 当前
    @Column(type = "char", len = 10, comment = "国家")
    private String country;

    @Column(type = "char", len = 10, comment = "国家")
    private String nation; // 前端显示，并可以修改

    @Column(type = "varchar", len = 36, comment = "平台")
    private String platform;

    @Column(type = "char", len = 24)
    private String ip;

    @Column(type = "varchar", len = 24)
    private String appVer;

    @Column(type = "text", isJson = true)
    private Map<Integer, String> tutorial = new HashMap<>();

    @Column
    private long loginTime;

    @Column(comment = "属性刷新时间")
    private long freshTime;

    @Column(comment = "保留字段")
    private long reserved;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceId0() {
        return deviceId0;
    }

    public void setDeviceId0(String deviceId0) {
        this.deviceId0 = deviceId0;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getGaid0() {
        return gaid0;
    }

    public void setGaid0(String gaid0) {
        this.gaid0 = gaid0;
    }

    public String getGaid() {
        return gaid;
    }

    public void setGaid(String gaid) {
        this.gaid = gaid;
    }

    public short getVip() {
        return vip;
    }

    public void setVip(short vip) {
        this.vip = vip;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getDiamond() {
        return diamond;
    }

    public void setDiamond(long diamond) {
        this.diamond = diamond;
    }

    public long getPayDiamond() {
        return payDiamond;
    }

    public void setPayDiamond(long payDiamond) {
        this.payDiamond = payDiamond;
    }

    public long getGold() {
        return gold;
    }

    public void setGold(long gold) {
        this.gold = gold;
    }

    public long getHonor() {
        return honor;
    }

    public void setHonor(long honor) {
        this.honor = honor;
    }

    public long getMedal() {
        return medal;
    }

    public void setMedal(long medal) {
        this.medal = medal;
    }

    public long getFriendship() {
        return friendship;
    }

    public void setFriendship(long friendship) {
        this.friendship = friendship;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public long getPowerRenewTime() {
        return powerRenewTime;
    }

    public void setPowerRenewTime(long powerRenewTime) {
        this.powerRenewTime = powerRenewTime;
    }

    public int getPvpPower() {
        return pvpPower;
    }

    public void setPvpPower(int pvpPower) {
        this.pvpPower = pvpPower;
    }

    public long getPvpPowerRenewTime() {
        return pvpPowerRenewTime;
    }

    public void setPvpPowerRenewTime(long pvpPowerRenewTime) {
        this.pvpPowerRenewTime = pvpPowerRenewTime;
    }

    public int getModId() {
        return modId;
    }

    public void setModId(int modId) {
        this.modId = modId;
    }

    public int getOriginSid() {
        return originSid;
    }

    public void setOriginSid(int originSid) {
        this.originSid = originSid;
    }

    public int getCurrentSid() {
        return currentSid;
    }

    public void setCurrentSid(int currentSid) {
        this.currentSid = currentSid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public long getBannedtime() {
        return bannedtime;
    }

    public void setBannedtime(long bannedtime) {
        this.bannedtime = bannedtime;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public long getChatbannedtime() {
        return chatbannedtime;
    }

    public void setChatbannedtime(long chatbannedtime) {
        this.chatbannedtime = chatbannedtime;
    }

    public short getChatbanned() {
        return chatbanned;
    }

    public void setChatbanned(short chatbanned) {
        this.chatbanned = chatbanned;
    }

    public int getPowerBuyTimes() {
        return powerBuyTimes;
    }

    public void setPowerBuyTimes(int powerBuyTimes) {
        this.powerBuyTimes = powerBuyTimes;
    }

    public long getPowerBuyDate() {
        return powerBuyDate;
    }

    public void setPowerBuyDate(long powerBuyDate) {
        this.powerBuyDate = powerBuyDate;
    }

    public int getGoldBuyTimes() {
        return goldBuyTimes;
    }

    public void setGoldBuyTimes(int goldBuyTimes) {
        this.goldBuyTimes = goldBuyTimes;
    }

    public long getGoldBuyDate() {
        return goldBuyDate;
    }

    public void setGoldBuyDate(long goldBuyDate) {
        this.goldBuyDate = goldBuyDate;
    }

    public int getNameChangedTimes() {
        return nameChangedTimes;
    }

    public void setNameChangedTimes(int nameChangedTimes) {
        this.nameChangedTimes = nameChangedTimes;
    }

    public String getCountry0() {
        return country0;
    }

    public void setCountry0(String country0) {
        this.country0 = country0;
    }

    public String getPlatform0() {
        return platform0;
    }

    public void setPlatform0(String platform0) {
        this.platform0 = platform0;
    }

    public String getIp0() {
        return ip0;
    }

    public void setIp0(String ip0) {
        this.ip0 = ip0;
    }

    public String getAppVer0() {
        return appVer0;
    }

    public void setAppVer0(String appVer0) {
        this.appVer0 = appVer0;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAppVer() {
        return appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

    public Map<Integer, String> getTutorial() {
        return tutorial;
    }

    public void setTutorial(Map<Integer, String> tutorial) {
        this.tutorial = tutorial;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public long getFreshTime() {
        return freshTime;
    }

    public void setFreshTime(long freshTime) {
        this.freshTime = freshTime;
    }

    public void setReserved(long reserved) {
        this.reserved = reserved;
    }

    public long getReserved(long mask) {
        try {
            int idx = getMaskIdx(mask);
            return ((reserved & mask) >> idx);
        } catch (Exception e) {
            return -1;
        }
    }

    public void setReserved(long mask, long value) throws Exception {
        int idx = getMaskIdx(mask);
        reserved = ((reserved & ~mask) | (value << idx));
    }

    private int getMaskIdx(long mask) throws Exception {
        int idx = 0;
        while (idx < 64) {
            if ((mask & (0x01l << idx)) != 0) break;
            ++idx;
        }
        if (idx >= 64) throw new Exception("mask error");
        return idx;
    }

    @Override
    public String toString() {
        return "PlayerModel{" + "account='" + account + '\'' + ", name='" + name + '\'' + ", deviceId='" + deviceId + '\'' + ", gaid='" + gaid + '\'' + ", vip=" + vip + ", level=" + level + ", diamond=" + diamond + ", payDiamond=" + payDiamond + ", gold=" + gold + ", honor=" + honor + ", medal=" + medal + ", friendship=" + friendship + ", power=" + power + ", powerRenewTime=" + powerRenewTime + ", pvpPower=" + pvpPower + ", pvpPowerRenewTime=" + pvpPowerRenewTime + ", modId=" + modId + ", originSid=" + originSid + ", currentSid=" + currentSid + ", createtime=" + createtime + ", bannedtime=" + bannedtime + ", status=" + status + ", chatbannedtime=" + chatbannedtime + ", chatbanned=" + chatbanned + ", powerBuyTimes=" + powerBuyTimes + ", powerBuyDate=" + powerBuyDate + ", goldBuyTimes=" + goldBuyTimes + ", goldBuyDate=" + goldBuyDate + ", nameChangedTimes=" + nameChangedTimes + ", deviceId0='" + deviceId0 + '\'' + ", gaid0='" + gaid0 + '\'' + ", country0='" + country0 + '\'' + ", platform0='" + platform0 + '\'' + ", ip0='" + ip0 + '\'' + ", appVer0='" + appVer0 + '\'' + ", country='" + country + '\'' + ", nation='" + nation + '\'' + ", platform='" + platform + '\'' + ", ip='" + ip + '\'' + ", appVer='" + appVer + '\'' + ", tutorial=" + tutorial + ", loginTime=" + loginTime + ", freshTime=" + freshTime + ", reserved=" + reserved + '}';
    }
}
