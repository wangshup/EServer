package com.dd.game.exceptions;

public class GameException extends Exception {

    private static final long serialVersionUID = -3464320023735291595L;

    private GameExceptionCode exceptionCode;
    private String exceptionInfo;

    public GameException() {
        super("Unknown Error");
        exceptionCode = GameExceptionCode.INVALID_OPT;
    }

    public GameException(GameExceptionCode exceptionCode) {
        super("Unknown Error");
        this.exceptionCode = exceptionCode;
    }

    public GameException(GameExceptionCode exceptionCode, String exceptionDesc) {
        super(exceptionDesc);
        this.exceptionCode = exceptionCode;
    }

    public GameException(GameExceptionCode exceptionCode, String exceptionDesc, String exceptionInfo) {
        super(exceptionDesc);
        this.exceptionCode = exceptionCode;
        this.exceptionInfo = exceptionInfo;
    }

    public GameExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public String getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    @Override
    public String toString() {
        return "GameException [exceptionCode=" + exceptionCode + ", exceptionInfo=" + exceptionInfo + "]";
    }
}
