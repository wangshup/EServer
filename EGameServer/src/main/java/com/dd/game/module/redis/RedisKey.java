package com.dd.game.module.redis;

import java.util.concurrent.TimeUnit;

import com.dd.game.utils.Constants;

public final class RedisKey {

    private RedisKey() {
    }

    public static final long REDIS_PLAYER_CACHE_EXPIRE_TIME = TimeUnit.DAYS.toSeconds(30);

    public static String playerHashCacheKey(long id) {
        return "player." + id;
    }

    // 玩家等级排名
    public static String playerLevelSet() {
        return "player.level." + Constants.SERVER_ID;
    }

    // 缓存最近三天登陆的玩家信息，供推荐好友用
    public static String friendSearchPlayerSet() {
        return "friend.search.set." + Constants.SERVER_ID;
    }

    // 缓存24小时内自己发出过的好友申请次数，每天可发的申请次数有上限
    public static String friendRequestTotalNum(long id) {
        return "friend.add.total.times." + id + "." + Constants.SERVER_ID;
    }

    // 缓存24小时内加过同一个好友的次数，同一个好友每天允许添加次数有上限
    public static String friendRequestNum(long id, long friendId) {
        return "friend.add.times." + id + "_" + friendId + "." + Constants.SERVER_ID;
    }

    // 好友：最近联系人列表
    // 注意：要一直保存，不要设置过期时间，如果机器故障，允许这部分数据丢失
    public static String friendChatList(long id) {
        return "friend.list.chat." + id + "." + Constants.SERVER_ID;
    }

    // 好友：最近组队列表
    // 注意：要一直保存，不要设置过期时间，如果机器故障，允许这部分数据丢失
    public static String friendTeamList(long id) {
        return "friend.list.team." + id + "." + Constants.SERVER_ID;
    }

    // 每天创建公会的人数
    public static String allianceCreatePlayers() {
        return "alliance.daily.create." + Constants.SERVER_ID;
    }

    // 每天加入公会的人数
    public static String allianceJoinPlayers() {
        return "alliance.daily.join." + Constants.SERVER_ID;
    }

    // 每天申请公会的人数
    public static String allianceApplyPlayers() {
        return "alliance.daily.apply." + Constants.SERVER_ID;
    }

    // 每天申请公会的次数
    public static String allianceApplyTimes() {
        return "alliance.daily.applyTotal." + Constants.SERVER_ID;
    }

    // 每天邀请加入公会的人数
    public static String allianceInvitePlayers() {
        return "alliance.daily.invite." + Constants.SERVER_ID;
    }

    // 每天邀请加入公会的次数
    public static String allianceInviteTimes() {
        return "alliance.daily.inviteTotal." + Constants.SERVER_ID;
    }

    // 每天踢出公会的人数
    public static String allianceKickPlayer() {
        return "alliance.kick." + Constants.SERVER_ID;
    }

    // 每天退出公会的人数
    public static String allianceLeavePlayer() {
        return "alliance.leave." + Constants.SERVER_ID;
    }

    // 每天解散公会的数量
    public static String allianceDismiss() {
        return "alliance.dismiss." + Constants.SERVER_ID;
    }

    // 竞技场排行榜
    public static String arenaRank() {
        return "arena.rank." + Constants.SERVER_ID;
    }

    // challenge排行榜
    public static String challengeRank() {
        return "challenge.rank" + Constants.SERVER_ID;
    }

}
