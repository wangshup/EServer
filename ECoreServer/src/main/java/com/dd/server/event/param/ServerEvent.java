package com.dd.server.event.param;

import java.util.Map;

public class ServerEvent implements IServerEvent {
    private final String eventType;
    private final Map<ServerEventParam, Object> params;

    public ServerEvent(String eventType) {
        this(eventType, null);
    }

    public ServerEvent(String eventType, Map<ServerEventParam, Object> params) {
        this.eventType = eventType;
        this.params = params;
    }

    public String getType() {
        return this.eventType;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(IServerEventParam id) {
        Object param = null;
        if (params != null) {
            param = this.params.get(id);
        }

        return (T) param;
    }

    public String toString() {
        return String.format("{ %s, Params: %s }",
                new Object[] { this.eventType, this.params != null ? this.params.keySet() : "none" });
    }
}