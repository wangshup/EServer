package com.dd.game.entity.player;

import com.dd.edata.db.DBWhere;
import com.dd.game.core.GameEngine;
import com.dd.game.entity.BaseEntity;
import com.dd.game.entity.LoginLog;
import com.dd.game.entity.ServerInfo;
import com.dd.game.entity.model.PlayerModel;
import com.dd.game.exceptions.GameException;
import com.dd.game.exceptions.GameExceptionCode;
import com.dd.game.module.event.EventDispatcher;
import com.dd.game.module.event.EventType;
import com.dd.game.module.redis.RedisKey;
import com.dd.game.network.handler.AbstractRequestHandler;
import com.dd.game.utils.Constants;
import com.dd.game.utils.ConstantsPush;
import com.dd.game.utils.DateTimeUtil;
import com.dd.protobuf.PBStructProtocol.PBPlayerInfo;
import com.dd.server.entities.IUser;
import com.dd.server.exceptions.ServerErrorCode;
import com.dd.server.exceptions.ServerErrorData;
import com.dd.server.exceptions.ServerLoginException;
import com.dd.server.request.Response;
import com.dd.server.utils.ClientDisconnectionReason;
import com.dd.server.utils.ECacheBuilder.ICacheable;
import com.dd.server.utils.IdWorker;
import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class Player extends BaseEntity implements ICacheable<Long> {
    private static final Logger logger = LoggerFactory.getLogger(Player.class);

    private LoginLog loginLog;
    private IUser user;
    private int protoVer;

    public Player(PlayerModel model) {
        super(model);
    }

    public static Player preRegister(String account, String ip, String deviceId, String gaid, String country, String platform, String appVer) throws Exception {
        Calendar cl = Calendar.getInstance();
        // player info
        Player player = new Player(new PlayerModel());
        player.setId(IdWorker.nextId(Constants.SERVER_ID));
        player.setAccount(account);
        player.setDeviceId0(deviceId);
        player.setDeviceId(deviceId);
        player.setGaid0(gaid);
        player.setGaid(gaid);
        player.setName("");
        player.setLevel(1);
        player.setCreatetime(cl.getTime());
        player.setIp0(ip);
        player.setCountry0(country);
        player.setPlatform0(platform);
        player.setAppVer0(appVer);
        player.setIp(ip);
        player.setCountry(country);
        player.setNation(country);
        player.setPlatform(platform);
        player.setAppVer(appVer);
        PlayerManager.getInstance().cachePlayer(player);
        EventDispatcher.fire(EventType.PLAYER_PRE_REGISTER, player);
        return player;
    }

    public static Player login(long id, String ip, String deviceId, String gaid, String country, String platform, String appVer) throws Exception {
        Player player = PlayerManager.getInstance().getPlayer(id);
        if (player != null) {
            long now = System.currentTimeMillis();
            // 验证是否被封号
            if (PlayerBannedStatus.BANNED.getValue() == player.getStatus()) {
                if (player.getBannedtime() < System.currentTimeMillis()) {
                    // 解除封号
                    player.setStatus(PlayerBannedStatus.NORMAL.getValue());
                    player.setBannedtime(0);
                } else {
                    throw new ServerLoginException(new StringBuilder().append("player: ").append(id).append(" banned").toString(), new ServerErrorData(ServerErrorCode.LOGIN_BANNED_USER));
                }
            }

            // 上次登录时间
            long lastLoginTime = player.getLoginTime();
            player.setDeviceId(deviceId);
            if (StringUtils.isBlank(player.getGaid0())) {
                player.setGaid0(gaid);
            }
            player.setGaid(gaid);
            player.setAppVer(appVer);
            player.setIp(ip);
            player.setCountry(country);
            player.setPlatform(platform);
            player.setLoginTime(now);
            player.update(false);
            LoginLog loginLog = new LoginLog(player.getId(), deviceId, ip, player.getLevel(), gaid, country, platform, appVer);
            loginLog.insert(true);
            player.setLoginLog(loginLog);
            EventDispatcher.fire(EventType.PLAYER_LOGIN, player, lastLoginTime);
        }
        return player;
    }

    public static Player load(long id) throws Exception {
        DBWhere where = DBWhere.equal("id", id);
        PlayerModel playerModel = GameEngine.getEData().select(PlayerModel.class, where);
        if (playerModel == null) return null;
        Player player = new Player(playerModel);
        return player;
    }

    public synchronized void setLoginLog(LoginLog loginLog) {
        this.loginLog = loginLog;
    }

    public IUser getUser() {
        return user;
    }

    public void setUser(IUser user) {
        user.setProperty("uid", getId());
        this.user = user;
    }

    public int getProtoVer() {
        return protoVer;
    }

    public void setProtoVer(int protoVer) {
        this.protoVer = protoVer;
    }

    public synchronized void addPayDiamond(long increment, String comment) {
        if (increment <= 0) {
            throw new IllegalArgumentException("increment: " + increment + " (expected: > 0)");
        }
        long oldvalue = this.getDiamond();
        this.setPayDiamond(oldvalue + increment);
        logger.info("player {} add pay diamond {}, old value:{}, now:{}", getId(), increment, oldvalue, this.getDiamond());
    }

    public synchronized void addDiamond(long increment, String comment) {
        if (increment <= 0) {
            throw new IllegalArgumentException("increment: " + increment + " (expected: > 0)");
        }
        long oldvalue = this.getDiamond();
        this.setDiamond(oldvalue + increment);
        logger.info("player {} add diamond {}, old value:{}, now:{}", getId(), increment, oldvalue, this.getDiamond());
    }

    public synchronized void decDiamond(long decrement, String comment) throws Exception {
        if (decrement <= 0) {
            throw new IllegalArgumentException("decrement: " + decrement + " (expected: > 0)");
        }
        long oldDiamond = this.getDiamond();
        long oldPayDiamond = this.getPayDiamond();
        long diamond = oldDiamond + oldPayDiamond;
        if (diamond < decrement) {
            throw new GameException(GameExceptionCode.PLAYER_DIAMOND_NOT_ENOUGH, "player diamond is not enough");
        }

        long remain = oldDiamond - decrement;
        if (remain < 0) {
            this.setDiamond(0);
            this.setPayDiamond(oldPayDiamond + remain);
        } else {
            this.setDiamond(oldDiamond - decrement);
        }
        logger.info("player {} dec diamond {}, old diamond value:{}, now:{}; old pay diamond:{}, now:{}", getId(), decrement, oldDiamond, this.getDiamond(), oldPayDiamond, this.getPayDiamond());
    }

    public synchronized void addGold(long increment, String comment) {
        if (increment <= 0) {
            throw new IllegalArgumentException("increment: " + increment + " (expected: > 0)");
        }
        long oldvalue = this.getGold();
        this.setGold(oldvalue + increment);
        logger.info("player {} add gold {}, old value:{}, now:{}", getId(), increment, oldvalue, this.getGold());
    }

    public synchronized void decGold(long decrement, String comment) throws Exception {
        if (decrement <= 0) {
            throw new IllegalArgumentException("decrement: " + decrement + " (expected: > 0)");
        }

        long oldvalue = this.getGold();
        if (oldvalue < decrement) {
            throw new GameException(GameExceptionCode.PLAYER_GOLD_NOT_ENOUGH, "player gold is not enough");
        }

        this.setGold(oldvalue - decrement);
        logger.info("player {} dec gold {}, old value:{}, now:{}", getId(), decrement, oldvalue, this.getGold());
    }

    public void register() throws Exception {
        setPowerRenewTime(System.currentTimeMillis());

        StringBuffer sb = new StringBuffer();
        ServerInfo si = GameEngine.getInstance().getServerInfo();
        setName(sb.append("King").append(Long.toHexString(si.incrAndGetId())).append(0).append(Constants.SERVER_ID).toString());

        setLoginTime(System.currentTimeMillis());
        insert(false);
        EventDispatcher.fire(EventType.PLAYER_REGISTER, this);
        LoginLog loginLog = new LoginLog(getId(), getDeviceId(), getIp(), getLevel(), getGaid(), getCountry(), getPlatform(), getAppVer());
        loginLog.insert(true);
        setLoginLog(loginLog);
    }

    public void freshHour0() {
        // 跨天刷新的逻辑写在这里
        // 如果跨天时当前正在在线，会由定时器触发调用
        // 如果不在线，则在玩家下一次登陆时会触发调用
        if (!DateTimeUtil.isTheSameDay(System.currentTimeMillis(), getFreshTime())) {
            setFreshTime(System.currentTimeMillis());
            GameEngine.getEData().del(RedisKey.friendRequestTotalNum(getId()));
            setGoldBuyTimes(0);
            setGoldBuyDate(System.currentTimeMillis());
            setPowerBuyTimes(0);
            setPowerBuyDate(System.currentTimeMillis());
            send(ConstantsPush.PLAYER_INFO, toSimpleProtoBuf());
            update(true);
        }
    }

    public Map<String, String> updateRedisCache() {
        Map<String, String> map = Maps.newHashMap();
        map.put(PlayerRedisCache.KEY_NAME, getName());
        map.put(PlayerRedisCache.KEY_LEVEL, String.valueOf(getLevel()));
        map.put(PlayerRedisCache.KEY_COUNTRY, getNation());
        map.put(PlayerRedisCache.KEY_LOGINTIME, String.valueOf(getLoginTime()));
        GameEngine.getEData().hmset(RedisKey.playerHashCacheKey(getId()), map, (int) RedisKey.REDIS_PLAYER_CACHE_EXPIRE_TIME);
        return map;
    }

    /**
     * 玩家redis等级排名，通用
     */
    public void updateRedisLevelSet() {
        GameEngine.getEData().zadd(RedisKey.playerLevelSet(), getLevel(), String.valueOf(getId()));
    }

    public synchronized void disconnect(ClientDisconnectionReason reason) throws Exception {
        if (reason == ClientDisconnectionReason.KICK_QUIET) return;
        this.user = null;
        if (loginLog != null) {
            loginLog.setLogoutLevel(getLevel());
            loginLog.setLogoutTime(System.currentTimeMillis());
            loginLog.setLogoutDiamond(this.getDiamond());
            loginLog.setLogoutPower(this.getPower());
            loginLog.update(true);
        }
    }

    @Override
    public Message toProtoBuf(int op) {
        PBPlayerInfo.Builder playerBuilder = toSimpleProtoBuf().toBuilder();
        return playerBuilder.build();
    }

    public PBPlayerInfo toSimpleProtoBuf() {
        PBPlayerInfo.Builder playerBuilder = PBPlayerInfo.newBuilder();
        playerBuilder.setUserId(String.valueOf(getId()));
        playerBuilder.setLevel(getLevel());
        playerBuilder.setDiamond(String.valueOf(getDiamond()));
        playerBuilder.setPayDiamond(String.valueOf(getPayDiamond()));
        playerBuilder.setGold(String.valueOf(getGold()));
        playerBuilder.setPower(getPower());
        playerBuilder.setHonor(String.valueOf(getHonor()));
        playerBuilder.setMedal(String.valueOf(getMedal()));
        playerBuilder.setFriendship(String.valueOf(getFriendship()));
        playerBuilder.setNick(getName());
        playerBuilder.setPowerBuyTimes(getPowerBuyTimes());
        playerBuilder.setGoldBuyTimes(getGoldBuyTimes());
        return playerBuilder.build();
    }

    public boolean send(String cmd, Message msg) {
        if (user == null) return false;
        Response.send(cmd, msg, user);
        AbstractRequestHandler.logRequestMsg("PUSH", cmd, null, msg, getId(), Constants.SERVER_ID, System.currentTimeMillis());
        return true;
    }

    @Override
    public String toString() {
        return "Player{" + "model=" + model + '}';
    }

    @Override
    public Long getK() {
        return getId();
    }

    //model delegate


    public String getAccount() {
        return ((PlayerModel) model).getAccount();
    }

    public void setAccount(String account) {
        ((PlayerModel) model).setAccount(account);
    }

    public String getName() {
        return ((PlayerModel) model).getName();
    }

    public void setName(String name) {
        ((PlayerModel) model).setName(name);
    }

    public String getDeviceId0() {
        return ((PlayerModel) model).getDeviceId0();
    }

    public void setDeviceId0(String deviceId0) {
        ((PlayerModel) model).setDeviceId0(deviceId0);
    }

    public String getDeviceId() {
        return ((PlayerModel) model).getDeviceId();
    }

    public void setDeviceId(String deviceId) {
        ((PlayerModel) model).setDeviceId(deviceId);
    }

    public String getGaid0() {
        return ((PlayerModel) model).getGaid0();
    }

    public void setGaid0(String gaid0) {
        ((PlayerModel) model).setGaid0(gaid0);
    }

    public String getGaid() {
        return ((PlayerModel) model).getGaid();
    }

    public void setGaid(String gaid) {
        ((PlayerModel) model).setGaid(gaid);
    }

    public short getVip() {
        return ((PlayerModel) model).getVip();
    }

    public void setVip(short vip) {
        ((PlayerModel) model).setVip(vip);
    }

    public int getLevel() {
        return ((PlayerModel) model).getLevel();
    }

    public void setLevel(int level) {
        ((PlayerModel) model).setLevel(level);
    }

    public long getDiamond() {
        return ((PlayerModel) model).getDiamond();
    }

    public void setDiamond(long diamond) {
        ((PlayerModel) model).setDiamond(diamond);
    }

    public long getPayDiamond() {
        return ((PlayerModel) model).getPayDiamond();
    }

    public void setPayDiamond(long payDiamond) {
        ((PlayerModel) model).setPayDiamond(payDiamond);
    }

    public long getGold() {
        return ((PlayerModel) model).getGold();
    }

    public void setGold(long gold) {
        ((PlayerModel) model).setGold(gold);
    }

    public long getHonor() {
        return ((PlayerModel) model).getHonor();
    }

    public void setHonor(long honor) {
        ((PlayerModel) model).setHonor(honor);
    }

    public long getMedal() {
        return ((PlayerModel) model).getMedal();
    }

    public void setMedal(long medal) {
        ((PlayerModel) model).setMedal(medal);
    }

    public long getFriendship() {
        return ((PlayerModel) model).getFriendship();
    }

    public void setFriendship(long friendship) {
        ((PlayerModel) model).setFriendship(friendship);
    }

    public int getPower() {
        return ((PlayerModel) model).getPower();
    }

    public void setPower(int power) {
        ((PlayerModel) model).setPower(power);
    }

    public long getPowerRenewTime() {
        return ((PlayerModel) model).getPowerRenewTime();
    }

    public void setPowerRenewTime(long powerRenewTime) {
        ((PlayerModel) model).setPowerRenewTime(powerRenewTime);
    }

    public int getPvpPower() {
        return ((PlayerModel) model).getPvpPower();
    }

    public void setPvpPower(int pvpPower) {
        ((PlayerModel) model).setPvpPower(pvpPower);
    }

    public long getPvpPowerRenewTime() {
        return ((PlayerModel) model).getPvpPowerRenewTime();
    }

    public void setPvpPowerRenewTime(long pvpPowerRenewTime) {
        ((PlayerModel) model).setPvpPowerRenewTime(pvpPowerRenewTime);
    }

    public int getModId() {
        return ((PlayerModel) model).getModId();
    }

    public void setModId(int modId) {
        ((PlayerModel) model).setModId(modId);
    }

    public int getOriginSid() {
        return ((PlayerModel) model).getOriginSid();
    }

    public void setOriginSid(int originSid) {
        ((PlayerModel) model).setOriginSid(originSid);
    }

    public int getCurrentSid() {
        return ((PlayerModel) model).getCurrentSid();
    }

    public void setCurrentSid(int currentSid) {
        ((PlayerModel) model).setCurrentSid(currentSid);
    }

    public Date getCreatetime() {
        return ((PlayerModel) model).getCreatetime();
    }

    public void setCreatetime(Date createtime) {
        ((PlayerModel) model).setCreatetime(createtime);
    }

    public long getBannedtime() {
        return ((PlayerModel) model).getBannedtime();
    }

    public void setBannedtime(long bannedtime) {
        ((PlayerModel) model).setBannedtime(bannedtime);
    }

    public short getStatus() {
        return ((PlayerModel) model).getStatus();
    }

    public void setStatus(short status) {
        ((PlayerModel) model).setStatus(status);
    }

    public long getChatbannedtime() {
        return ((PlayerModel) model).getChatbannedtime();
    }

    public void setChatbannedtime(long chatbannedtime) {
        ((PlayerModel) model).setChatbannedtime(chatbannedtime);
    }

    public short getChatbanned() {
        return ((PlayerModel) model).getChatbanned();
    }

    public void setChatbanned(short chatbanned) {
        ((PlayerModel) model).setChatbanned(chatbanned);
    }

    public int getPowerBuyTimes() {
        return ((PlayerModel) model).getPowerBuyTimes();
    }

    public void setPowerBuyTimes(int powerBuyTimes) {
        ((PlayerModel) model).setPowerBuyTimes(powerBuyTimes);
    }

    public long getPowerBuyDate() {
        return ((PlayerModel) model).getPowerBuyDate();
    }

    public void setPowerBuyDate(long powerBuyDate) {
        ((PlayerModel) model).setPowerBuyDate(powerBuyDate);
    }

    public int getGoldBuyTimes() {
        return ((PlayerModel) model).getGoldBuyTimes();
    }

    public void setGoldBuyTimes(int goldBuyTimes) {
        ((PlayerModel) model).setGoldBuyTimes(goldBuyTimes);
    }

    public long getGoldBuyDate() {
        return ((PlayerModel) model).getGoldBuyDate();
    }

    public void setGoldBuyDate(long goldBuyDate) {
        ((PlayerModel) model).setGoldBuyDate(goldBuyDate);
    }

    public int getNameChangedTimes() {
        return ((PlayerModel) model).getNameChangedTimes();
    }

    public void setNameChangedTimes(int nameChangedTimes) {
        ((PlayerModel) model).setNameChangedTimes(nameChangedTimes);
    }

    public String getCountry0() {
        return ((PlayerModel) model).getCountry0();
    }

    public void setCountry0(String country0) {
        ((PlayerModel) model).setCountry0(country0);
    }

    public String getPlatform0() {
        return ((PlayerModel) model).getPlatform0();
    }

    public void setPlatform0(String platform0) {
        ((PlayerModel) model).setPlatform0(platform0);
    }

    public String getIp0() {
        return ((PlayerModel) model).getIp0();
    }

    public void setIp0(String ip0) {
        ((PlayerModel) model).setIp0(ip0);
    }

    public String getAppVer0() {
        return ((PlayerModel) model).getAppVer0();
    }

    public void setAppVer0(String appVer0) {
        ((PlayerModel) model).setAppVer0(appVer0);
    }

    public String getCountry() {
        return ((PlayerModel) model).getCountry();
    }

    public void setCountry(String country) {
        ((PlayerModel) model).setCountry(country);
    }

    public String getNation() {
        return ((PlayerModel) model).getNation();
    }

    public void setNation(String nation) {
        ((PlayerModel) model).setNation(nation);
    }

    public String getPlatform() {
        return ((PlayerModel) model).getPlatform();
    }

    public void setPlatform(String platform) {
        ((PlayerModel) model).setPlatform(platform);
    }

    public String getIp() {
        return ((PlayerModel) model).getIp();
    }

    public void setIp(String ip) {
        ((PlayerModel) model).setIp(ip);
    }

    public String getAppVer() {
        return ((PlayerModel) model).getAppVer();
    }

    public void setAppVer(String appVer) {
        ((PlayerModel) model).setAppVer(appVer);
    }

    public Map<Integer, String> getTutorial() {
        return ((PlayerModel) model).getTutorial();
    }

    public void setTutorial(Map<Integer, String> tutorial) {
        ((PlayerModel) model).setTutorial(tutorial);
    }

    public long getLoginTime() {
        return ((PlayerModel) model).getLoginTime();
    }

    public void setLoginTime(long loginTime) {
        ((PlayerModel) model).setLoginTime(loginTime);
    }

    public long getFreshTime() {
        return ((PlayerModel) model).getFreshTime();
    }

    public void setFreshTime(long freshTime) {
        ((PlayerModel) model).setFreshTime(freshTime);
    }

    public long getReserved(long mask) {
        return ((PlayerModel) model).getReserved(mask);
    }

    public void setReserved(long mask, long value) throws Exception {
        ((PlayerModel) model).setReserved(mask, value);
    }

    public long getId() {
        return model.getId();
    }

    public void setId(long id) {
        model.setId(id);
    }

    public enum PlayerBannedStatus {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 封号
         */
        BANNED(1);

        private short status;

        private PlayerBannedStatus(int status) {
            this.status = (short) status;
        }

        public short getValue() {
            return status;
        }
    }
}
