package com.dd.server.extensions;

/**
 * Author: shushenglin
 * Date:   2017/3/31 09:17
 */
public enum ExtensionReloadMode {
	NONE,
	MANUAL,
	AUTO,
	DISABLE
	;

	public static ExtensionReloadMode valueOf(int val){
		switch (val) {
			case 1:
				return MANUAL;
			case 2:
				return AUTO;
			case 3:
				return DISABLE;
			default:
				return NONE;
		}
	}
}
