package com.dd.game.core.module.event;

public class ObjectEvent {
	private Object objData;
	private int eventType;

	/**
	 * @param obj
	 *            系统默认参数
	 * @param objData
	 *            自定义参数
	 * @param eventType
	 *            事件健值
	 */
	public ObjectEvent(Object objData, int eventType) {
		this.objData = objData;
		this.eventType = eventType;
	}

	public void setObject(Object objData) {
		this.objData = objData;
	}

	public Object getObject() {
		return this.objData;
	}

	public int getEventType() {
		return eventType;
	}
}
