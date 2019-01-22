package com.dd.game.exceptions;

public enum GameExceptionCode {
    INVALID_OPT(1001), // 无效操作
    PLAYER_DIAMOND_NOT_ENOUGH(1002), // 玩家钻石不足
    PLAYER_GOLD_NOT_ENOUGH(1003), // 玩家金币不足
    MARCH_TYPE_ERROR(800001),//
    MARCH_COUNT_ERROR(800002),//
    MARCH_SIZE_ERROR(800003),//
    MARCH_STATE_ERROR(800004),//
    MARCH_RES_ERROR(800005),//
    MARCH_CURRENCY_ERROR(800006),//
    MARCH_HERO_ERROR(800020),//
    MARCH_HERO_CITY_ERROR(800021),//
    MARCH_HERO_COUNT_ERROR(800022),//
    MARCH_HERO_STATE_ERROR(800023),//
    MARCH_TROOP_ERROR(800030),//
    MARCH_TROOP_COUNT_ERROR(800031),//
    MARCH_TARGET_ERROR(800040),//
    MARCH_TARGET_TYPE_ERROR(800041),//
    MARCH_TARGET_SAME_ERROR(800042),//
    MARCH_ACCELERATE_ITEM_ERROR(800043),//
    WORLD_POINT_TYPE_ERROR(800101),//
    WORLD_POINT_HAS_OCCUPIED(800102),//
    WORLD_POINT_OBJ_INVALID(800103),//
    WORLD_POINT_FULL(800104),//
    WORLD_TRANSITION_ERROR(800105),//
    ;

    private int exceptionCode;

    GameExceptionCode(int errorCode) {
        this.exceptionCode = errorCode;
    }

    public int getCode() {
        return exceptionCode;
    }

}
