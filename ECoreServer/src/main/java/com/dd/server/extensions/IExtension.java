package com.dd.server.extensions;

import java.io.IOException;
import java.util.Properties;

import com.dd.server.entities.IUser;
import com.dd.server.entities.IZone;
import com.dd.server.event.param.IServerEvent;
import com.dd.server.exceptions.BootException;
import com.dd.server.exceptions.EventHandleException;
import com.dd.server.request.Request;
import com.google.protobuf.Message;

public interface IExtension {

    void init() throws BootException;

    void destroy();

    IZone getParentZone();

    void setZone(IZone zone);

    void setName(String paramString);

    String getName();

    void setExtensionFileName(String paramString);

    String getExtensionFileName();

    String getPropertiesFileName();

    void setPropertiesFileName(String paramString) throws IOException;

    Properties getConfigProperties();

    void setState(ExtensionState state);

    ExtensionState getState();

    ExtensionReloadMode getReloadMode();

    void setReloadMode(ExtensionReloadMode reloadMode);

    void handleRequest(IUser user, Request msg) throws Exception;

    Message handleServerEvent(IServerEvent event) throws EventHandleException;
}
