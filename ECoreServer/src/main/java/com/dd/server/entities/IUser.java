package com.dd.server.entities;

import com.dd.server.session.ISession;
import com.dd.server.utils.ClientDisconnectionReason;

public interface IUser {
    int getId();

    String getUid();

    ISession getSession();

    Object getProperty(Object paramObject);

    void setProperty(Object paramObject1, Object paramObject2);

    boolean containsProperty(Object paramObject);

    void removeProperty(Object paramObject);

    void disconnect(ClientDisconnectionReason reason);

    void doDisconnect(ClientDisconnectionReason reason);

    boolean isConnected();

    String getIpAddress();

    boolean isTimeout();

    IZone getZone();

    void setZone(IZone paramZone);

    void removeFromZone();
}