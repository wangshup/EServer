package com.dd.game.core.module.event;

public interface Publisher {

	void notifyListeners(ObjectEvent event);

}