package com.dd.server.entities;

import com.dd.server.extensions.IExtension;
import com.dd.server.session.ISession;

import java.util.List;

public interface IZone {
    List<IUser> getUserList();

    int getZoneId();

    String getName();

    int getUserCount();

    Object getProperty(String paramString);

    void setProperty(String key, Object value);

    IExtension getExtension();

    void setExtension(IExtension paramIServerExtension);

    void removeUser(IUser user);

    void signalExtensionReady();

    IExtension getRunningExtension();

    IUser addUser(IUser user);

    IUser getUserBySession(ISession session);

    void checkTimeoutUsers(boolean closeChannel);

    boolean containsSession(ISession session);

    IUser getUserByUid(long uid);

    String getUdpIp();

    void setUdpIp(String udpIp);

    int getUpdPort();

    void setUpdPort(int updPort);
}