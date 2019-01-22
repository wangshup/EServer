package com.dd.server.entities;

import com.dd.server.Server;
import com.dd.server.extensions.ExtensionState;
import com.dd.server.extensions.IExtension;
import com.dd.server.session.ISession;
import com.dd.server.utils.ClientDisconnectionReason;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class GameZone implements IZone {
    private final Map<ISession, IUser> sessionUsers = new ConcurrentHashMap<>();
    private final Map<String, IUser> users = new ConcurrentHashMap<>();
    private Logger logger = LoggerFactory.getLogger(GameZone.class);
    private Map<String, Object> properties = new ConcurrentHashMap<>();
    private IExtension extension;
    private int zoneId = 0;
    private String name;
    private String udpIp;
    private int updPort;
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public GameZone(String name, IExtension extension) {
        this.name = name;
        this.extension = extension;

        String zone = StringUtils.substring(name, StringUtils.lastIndexOf(name, "_") + 1);
        try {
            zoneId = Integer.parseInt(zone);
        } catch (Exception e) {
            logger.warn("parse zone id error, zone id default 0", e);
        }
        this.udpIp = Server.getInstance().getConfiguration().getString("udp.ip", "");
        this.updPort = Server.getInstance().getConfiguration().getInt("udp.port", 0);
    }

    public int getZoneId() {
        return zoneId;
    }

    public String getName() {
        return this.name;
    }

    public Object getProperty(String key) {
        return this.properties.get(key);
    }

    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    public IExtension getExtension() {
        return this.extension;
    }

    public void setExtension(IExtension extension) {
        this.extension = extension;
    }

    @Override
    public void signalExtensionReady() {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IExtension getRunningExtension() {
        if (extension.getState() == ExtensionState.RUNNING) {
            return extension;
        }
        lock.lock();
        try {
            if (extension.getState() == ExtensionState.RUNNING) {
                return extension;
            }
            while (extension.getState() != ExtensionState.RUNNING) {
                try {
                    logger.info("zone {} wait for running extension", getName());
                    condition.await(10, TimeUnit.SECONDS);
                } catch (Throwable e) {
                }
            }
        } finally {
            lock.unlock();
        }
        logger.info("zone {} ready for running extension", getName());
        return extension;
    }

    @Override
    public IUser addUser(IUser user) {
        sessionUsers.put(user.getSession(), user);
        return users.put(user.getUid(), user);
    }

    @Override
    public IUser getUserBySession(ISession session) {
        return (IUser) this.sessionUsers.get(session);
    }

    @Override
    public void removeUser(IUser user) {
        ISession session = user.getSession();
        if (session != null) {
            sessionUsers.remove(session);
        }

        users.remove(user.getUid(), user);
    }

    @Override
    public boolean containsSession(ISession session) {
        return this.sessionUsers.containsKey(session);
    }

    @Override
    public List<IUser> getUserList() {
        return new ArrayList<IUser>(this.sessionUsers.values());
    }

    @Override
    public int getUserCount() {
        return this.sessionUsers.size();
    }

    @Override
    public IUser getUserByUid(long uid) {
        return users.get(uid);
    }

    @Override
    public void checkTimeoutUsers(boolean closeChannel) {
        for (Iterator<Entry<ISession, IUser>> it = sessionUsers.entrySet().iterator(); it.hasNext(); ) {
            IUser user = it.next().getValue();
            if (user.isTimeout()) {
                if (closeChannel) user.disconnect(ClientDisconnectionReason.IDLE);
                else user.doDisconnect(ClientDisconnectionReason.IDLE);
                logger.info("User timeout task, close idle user: {}", user.toString());
            }
        }
    }

    public String getUdpIp() {
        return udpIp;
    }

    public void setUdpIp(String udpIp) {
        this.udpIp = udpIp;
    }

    public int getUpdPort() {
        return updPort;
    }

    public void setUpdPort(int updPort) {
        this.updPort = updPort;
    }
}