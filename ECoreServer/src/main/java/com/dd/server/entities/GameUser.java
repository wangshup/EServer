package com.dd.server.entities;

import com.dd.protobuf.CommonProtocol.PacketHead;
import com.dd.server.Server;
import com.dd.server.event.param.ServerEvent;
import com.dd.server.event.param.ServerEventParam;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.request.Response;
import com.dd.server.session.ISession;
import com.dd.server.utils.ClientDisconnectionReason;
import com.dd.server.utils.Constants;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameUser implements IUser {
    private static AtomicInteger autoIncId = new AtomicInteger(0);
    private final Map<Object, Object> properties;
    private Logger logger = LoggerFactory.getLogger(GameUser.class);
    private int id;
    private String uid;
    private ISession session;
    private IZone zone;

    public GameUser(ISession session, String uid, IZone zone) {
        this.id = getNewUserID();
        this.uid = uid;
        this.session = session;
        this.properties = new ConcurrentHashMap<>();
        this.zone = zone;
    }

    private static int getNewUserID() {
        return autoIncId.incrementAndGet();
    }

    public int getId() {
        return this.id;
    }

    public ISession getSession() {
        return this.session;
    }

    public Object getProperty(Object key) {
        return this.properties.get(key);
    }

    public void setProperty(Object key, Object val) {
        this.properties.put(key, val);
    }

    public String getUid() {
        return uid;
    }

    public boolean containsProperty(Object key) {
        return this.properties.containsKey(key);
    }

    public void removeProperty(Object key) {
        this.properties.remove(key);
    }

    public boolean isConnected() {
        return true;
    }

    public String getIpAddress() {
        return session.getAddress();
    }

    public boolean isTimeout() {
        if (this.session == null) {
            return true;
        }
        return System.currentTimeMillis() - this.session.getLastReadTime() > Constants.USER_INVALID_TIME_SEC * 1000;
    }

    public IZone getZone() {
        return this.zone;
    }

    public void setZone(IZone zone) {
        this.zone = zone;
    }

    @Override
    public String toString() {
        return "GameUser{" + "id=" + id + ", uid='" + uid + '\'' + ", session=" + session + ", zone=" + zone + '}';
    }

    @Override
    public void disconnect(ClientDisconnectionReason reason) {
        doDisconnect(reason);
        if (session != null) {
            if (session.getKcpOnUdp() != null) session.getKcpOnUdp().close(null);
            if (session.getChannel() != null && session.getChannel().isActive()) {
                PacketHead.Builder head = PacketHead.newBuilder();
                head.setActionId(ServerEventType.DISCONNECT);
                head.setErrorCode(reason.getValue());
                ChannelFuture future = session.writeResponse(new Response(head.build()));
                if (Server.getInstance().getMode() == Server.ServerMode.SERVER)
                    future.addListener(f -> session.getChannel().close());
            }
        }
    }

    @Override
    public void doDisconnect(ClientDisconnectionReason reason) {
        try {
            Map<ServerEventParam, Object> evtParams = new HashMap<>();
            evtParams.put(ServerEventParam.USER, this);
            evtParams.put(ServerEventParam.DISCONNECTION_REASON, reason);
            if (zone != null && zone.getExtension() != null)
                zone.getExtension().handleServerEvent(new ServerEvent(ServerEventType.DISCONNECT, evtParams));
        } catch (Exception e) {
            logger.error("user {} disconnect({}) error!", getUid(), reason, e);
        } finally {
            removeFromZone();
        }
    }

    @Override
    public void removeFromZone() {
        if (zone != null) {
            zone.removeUser(this);
        }
    }
}