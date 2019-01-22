package com.dd.game.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerStartStatus {

    private String startIp;
    private int status;

    public static ServerStartStatus valueOf(String startMac, int status) {
        ServerStartStatus serverStartStatus = new ServerStartStatus();
        serverStartStatus.startIp = startMac;
        serverStartStatus.status = status;
        return serverStartStatus;
    }

    public static ServerStartStatus getCurrent() {
        ServerStartStatus serverStartStatus = new ServerStartStatus();
        String localMac = "";
        try {
            localMac = getLocalMac();
        } catch (Exception e) {
            e.printStackTrace();
        }

        serverStartStatus.setStartIp(localMac);
        serverStartStatus.setStatus(SSStatus.STOPED.ordinal());
        return serverStartStatus;
    }

    private static String getLocalMac() throws SocketException, UnknownHostException {
        InetAddress ia = InetAddress.getLocalHost();
        return ia.getHostAddress().toUpperCase();
    }

    public String getStartIp() {
        return startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void update() {
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(this.startIp);
    }

    public boolean isRunning() {
        return this.status == SSStatus.RUNNING.ordinal();
    }

    public boolean isMacEqualsLocal() {
        String localMac = "";
        try {
            localMac = getLocalMac();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.startIp.equals(localMac);
    }

    public void setStoped() {
        this.status = SSStatus.STOPED.ordinal();
    }

    public void setStoping() {
        this.status = SSStatus.STOPING.ordinal();
    }

    public void setStart() {
        this.status = SSStatus.RUNNING.ordinal();
        String localMac = "";
        try {
            localMac = getLocalMac();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.startIp = localMac;
    }

    public static enum SSStatus {
        RUNNING, STOPING, STOPED
    }
}