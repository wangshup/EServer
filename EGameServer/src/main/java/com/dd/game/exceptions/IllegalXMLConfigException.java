package com.dd.game.exceptions;

/**
 * XML配置运行时异常
 */
public class IllegalXMLConfigException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IllegalXMLConfigException() {
		super();
	}

	public IllegalXMLConfigException(String s) {
		super(s);
	}

	public IllegalXMLConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalXMLConfigException(Throwable cause) {
		super(cause);
	}
}
