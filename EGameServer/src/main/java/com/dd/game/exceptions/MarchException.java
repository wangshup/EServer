package com.dd.game.exceptions;

/**
 * @program: server
 * @description: 行军异常类
 * @author: wangshupeng
 * @create: 2018-11-19 16:32
 **/
public class MarchException extends GameException {
    public MarchException(GameExceptionCode error) {
        super(error);
    }

    public MarchException(GameExceptionCode error, String info) {
        super(error, info);
    }
}