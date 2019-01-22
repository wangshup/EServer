package com.dd.game.module.event;

public enum EventType {

    CONFIG_RELOAD(1),

    PLAYER_PRE_REGISTER(999),

    PLAYER_REGISTER(1000),

    PLAYER_LOGIN(1001),

    PLAYER_LOGOUT(1002),

    PLAYER_LEVEL_UP(1003),

    PLAYER_CHANGE_NAME(1004),

    PLAYER_RECHARGE(1005),

    /**
     * 领主强化
     */
    PLAYER_STRONG(1006),

    /**
     * 修改头像
     */
    PLAYER_ALTER_MOD(1007),

    /**
     * 登陆后添加
     **/
    PLAYER_ZONE_JOIN(1008),

    /**
     * 修改玩家国旗
     */
    PLAYER_ALTER_NATION(1009),

    /**
     * 每小时触发一次
     */
    HOUR_TRIGGER(3001),

    /**
     * 每周一0点触发
     */
    WEEK_ZERO_HOUR_TRIGGER(3002);

    private int id;

    EventType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
