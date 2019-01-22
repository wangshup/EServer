package com.dd.game.core.module.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEvent implements Observer, Publisher {
	private static final Logger logger = LoggerFactory.getLogger(AbstractEvent.class);

	private static boolean DEBUG = true;
	private Object lock = new AbstractEventLock();
	private Hashtable<Integer, Collection<ObjectListener>> listeners;
	
	@Override
	public void addListener(ObjectListener objectListener, int eventType) {
		synchronized (lock) {
			if (listeners == null)
				listeners = new Hashtable<Integer, Collection<ObjectListener>>();
			if (listeners.get(eventType) == null) {
				Collection<ObjectListener> tempInfo = new HashSet<ObjectListener>();
				tempInfo.add(objectListener);
				listeners.put(eventType, tempInfo);
			} else {
				listeners.get(eventType).add(objectListener);
			}
			debugEventMsg("注册一个事件,类型为" + eventType);
		}
	}

	public void removeListener(ObjectListener objectListener, int eventType) {
		synchronized (lock) {
			if (listeners == null)
				return;
			Collection<ObjectListener> tempInfo = listeners.get(eventType);
			if (tempInfo != null) {
				tempInfo.remove(objectListener);
			}
		}
		debugEventMsg("移除一个事件,类型为" + eventType);
	}
	
	@Override
	public void notifyListeners(ObjectEvent event) {
		List<ObjectListener> tempList = null;
		synchronized (lock) {
			if (listeners == null)
				return;
			int eventType = event.getEventType();
			if (listeners.get(eventType) != null) {
				Collection<ObjectListener> tempInfo = listeners.get(eventType);
				tempList = new ArrayList<ObjectListener>();
				Iterator<ObjectListener> iter = tempInfo.iterator();
				while (iter.hasNext()) {
					ObjectListener listener = (ObjectListener) iter.next();
					tempList.add(listener);
				}
			}
		}
		// 触发
		if (tempList != null) {
			for (ObjectListener listener : tempList) {
				listener.onEvent(event);
			}
		}
	}

	public void clearListener() {
		synchronized (lock) {
			if (listeners != null) {
				listeners = null;
			}
		}
	}

	public void debugEventMsg(String msg) {
		if (DEBUG) {
			logger.info(msg);
		}
	}
}

class AbstractEventLock extends Object {

}