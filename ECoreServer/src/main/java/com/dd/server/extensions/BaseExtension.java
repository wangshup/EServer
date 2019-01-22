package com.dd.server.extensions;

import com.dd.server.annotation.MultiHandler;
import com.dd.server.entities.IUser;
import com.dd.server.entities.IZone;
import com.dd.server.event.IServerEventHandler;
import com.dd.server.event.param.IServerEvent;
import com.dd.server.event.param.ServerEventType;
import com.dd.server.exceptions.EventHandleException;
import com.dd.server.exceptions.ServerRuntimeException;
import com.dd.server.request.IClientRequestHandler;
import com.dd.server.request.Request;
import com.dd.server.request.RequestHandlerFactory;
import com.google.protobuf.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class BaseExtension implements IExtension {
    private static final Logger logger = LoggerFactory.getLogger(BaseExtension.class);
    private String configFileName;
    private Properties configProperties;
    private String name;
    private String extensionClassName;
    private RequestHandlerFactory handlerFactory = new RequestHandlerFactory(this);
    private ConcurrentMap<Object, Object> properties = new ConcurrentHashMap<Object, Object>();
    private IZone zone;
    private int zoneIndex;
    private volatile ExtensionState state = ExtensionState.CREATED;
    private ExtensionReloadMode reloadMode = ExtensionReloadMode.MANUAL;

    public int getZoneIndex() {
        return zoneIndex;
    }

    public void setZoneIndex(int zoneIndex) {
        this.zoneIndex = zoneIndex;
    }

    public void destroy() {
        this.handlerFactory.clearAll();
    }

    public String getPropertiesFileName() {
        return this.configFileName;
    }

    public void setPropertiesFileName(String fileName) throws IOException {
        if (this.configFileName != null) {
            throw new ServerRuntimeException(new StringBuilder().append("Cannot redefine properties file name of an extension: ").append(toString()).toString());
        }
        boolean isDefault = false;
        if ((StringUtils.isNotBlank(fileName)) && (!"config.properties".equals(fileName))) {
            this.configFileName = fileName;
        } else {
            isDefault = true;
            this.configFileName = "config.properties";
        }
        String fileToLoad = new StringBuilder().append("extensions/").append(this.name).append("/").append(this.configFileName).toString();
        logger.info(new StringBuilder().append("start load extension properties ").append(fileToLoad).toString());
        if (isDefault) loadDefaultConfigFile(fileToLoad);
        else loadCustomConfigFile(fileToLoad);
    }

    public Object getProperty(Object key) {
        return this.properties.get(key);
    }

    public void removeProperty(Object key) {
        this.properties.remove(key);
    }

    public void setProperty(Object key, Object value) {
        this.properties.put(key, value);
    }

    public boolean containsProperty(Object key) {
        return this.properties.containsKey(key);
    }

    public int getUserCount() {
        return zone.getUserCount();
    }

    public Collection<IUser> getUserList() {
        return zone.getUserList();
    }

    public String getExtensionFileName() {
        return this.extensionClassName;
    }

    public void setExtensionFileName(String className) {
        this.extensionClassName = className;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void handleRequest(IUser user, Request msg) throws Exception {
        String requestId = msg.getHead().getActionId();
        try {
            IClientRequestHandler handler = (IClientRequestHandler) handlerFactory.findHandler(requestId);
            if (handler == null) {
                throw new ServerRuntimeException(new StringBuilder().append("Request handler not found: '").append(requestId).append("'. Make sure the handler is registered in your extension using addRequestHandler()").toString());
            }
            if (handler.getClass().isAnnotationPresent(MultiHandler.class)) {
                msg.setMultiHandlerRequestId(requestId.substring(requestId.indexOf(RequestHandlerFactory.DOT_SEPARATOR) + 1));
            }

            Object pbMsg = handlerFactory.parse2PBMsg(requestId, (byte[]) msg.getBody(), msg.getBodyLen());
            msg.setBody(pbMsg);
            handler.handleClientRequest(user, msg);
        } catch (InstantiationException ex) {
            logger.warn("Cannot instantiate handler class: ", ex);
        } catch (IllegalAccessException var7) {
            logger.warn("Illegal access for handler class: ", var7);
        }
    }

    public Message handleServerEvent(IServerEvent event) throws EventHandleException {
        String handlerId = event.getType();
        try {
            IServerEventHandler handler = (IServerEventHandler) this.handlerFactory.findHandler(handlerId);
            if (handler == null) {
                throw new ServerRuntimeException(new StringBuilder().append("Event handler not found: '").append(handlerId).append("'. Make sure the handler is registered in your extension using addEventHandler()").toString());
            }

            return handler.handleServerEvent(event);
        } catch (InstantiationException var4) {
            logger.warn("Cannot instantiate handler class: ", var4);
        } catch (IllegalAccessException var5) {
            logger.warn("Illegal access for handler class: ", var5);
        }
        return null;
    }

    public void setZone(IZone zone) {
        this.zone = zone;
    }

    public void addRequestHandler(String requestId, Class<?> theClass) {
        if (!IClientRequestHandler.class.isAssignableFrom(theClass)) {
            throw new ServerRuntimeException(String.format("Provided Request Handler does not implement IClientRequestHandler: %s, Cmd: %s", new Object[]{theClass, requestId}));
        }
        this.handlerFactory.addHandler(requestId, theClass);
    }

    public void addEventHandler(String eventType, Class<?> theClass) {
        if (!IServerEventHandler.class.isAssignableFrom(theClass)) {
            throw new ServerRuntimeException(String.format("Provided Event Handler does not implement IServerEventHandler: %s, Cmd: %s", new Object[]{theClass, eventType.toString()}));
        }

        this.handlerFactory.addHandler(eventType, theClass);
    }

    public void addMsgParseClass(String requestId, Class<?> clazz) {
        this.handlerFactory.addParseClass(requestId, clazz);
    }

    public void removeRequestHandler(String requestId) {
        this.handlerFactory.removeHandler(requestId);
    }

    public void removeEventHandler(ServerEventType eventType) {
        this.handlerFactory.removeHandler(eventType.toString());
    }

    private void loadDefaultConfigFile(String fileName) throws IOException {
        this.configProperties = new Properties();
        try (InputStream is = new FileInputStream(fileName)) {
            this.configProperties.load(is);
        }
    }

    private void loadCustomConfigFile(String fileName) throws IOException {
        this.configProperties = new Properties();
        try (InputStream is = new FileInputStream(fileName)) {
            this.configProperties.load(is);
        }
    }

    public Properties getConfigProperties() {
        return this.configProperties;
    }

    public void trace(Object... args) {
        logger.info(getTraceMessage(args));
    }

    private String getTraceMessage(Object[] args) {
        StringBuilder traceMsg = new StringBuilder().append("{").append(this.name).append("}: ");
        for (Object o : args) {
            traceMsg.append(o.toString()).append(" ");
        }
        return traceMsg.toString();
    }

    public IZone getParentZone() {
        return this.zone;
    }

    @Override
    public ExtensionReloadMode getReloadMode() {
        return reloadMode;
    }

    @Override
    public void setReloadMode(ExtensionReloadMode reloadMode) {
        this.reloadMode = reloadMode;
    }

    @Override
    public ExtensionState getState() {
        return state;
    }

    @Override
    public void setState(ExtensionState state) {
        this.state = state;
    }
}