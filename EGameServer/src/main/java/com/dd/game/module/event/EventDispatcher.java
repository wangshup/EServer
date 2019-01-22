package com.dd.game.module.event;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class EventDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(EventDispatcher.class);

    private static Map<EventType, List<EventListener>> dispatcher = Maps.newHashMap();

    public static void addListener(EventType type, EventListener listener) {
        List<EventListener> list = dispatcher.get(type);
        if (list == null) {
            list = Lists.newArrayList();
            dispatcher.put(type, list);
        }
        list.add(listener);
    }

    public static void fire(EventType evtType, Object... params) {
        List<EventListener> list = dispatcher.get(evtType);
        if (list != null) {
            for (EventListener listener : list) {
                try {
                    listener.process(params);
                } catch (Exception e) {
                    logger.error("event type {},[{}] fire error", evtType, params, e);
                }
            }
        }
    }
}
