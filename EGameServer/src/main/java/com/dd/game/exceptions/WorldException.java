package com.dd.game.exceptions;

/**
 * @program: server
 * @description: 世界异常类
 * @author: wangshupeng
 * @create: 2018-11-19 16:32
 **/
public class WorldException extends GameException {
    public WorldException(GameExceptionCode error) {
        super(error);
    }

    public WorldException(GameExceptionCode error, String info) {
        super(error, info);
    }
}