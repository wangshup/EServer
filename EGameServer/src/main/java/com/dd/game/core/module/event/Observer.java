package com.dd.game.core.module.event;

/**
 * @author jiangmin.wu 
 * <p> 2017年1月9日 下午10:46:57 </p>
 */
public interface Observer {
	void addListener(ObjectListener objectListener, int eventType);

	void removeListener(ObjectListener objectListener, int eventType);
}