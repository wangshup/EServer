package com.dd.server.request;

import com.dd.server.annotation.MultiHandler;
import com.dd.server.extensions.BaseExtension;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHandlerFactory {
    public static final String DOT_SEPARATOR = ".";
    @SuppressWarnings("unused")
    private final BaseExtension parentExtension;
    private final Map<String, Class<?>> handlers = new ConcurrentHashMap<>();
    protected Map<String, Class<?>> parseClasses = new HashMap<>();

    public RequestHandlerFactory(BaseExtension ext) {
        this.parentExtension = ext;
    }

    public void addHandler(String handlerKey, Class<?> handlerClass) {
        this.handlers.put(handlerKey, handlerClass);
    }

    public synchronized void clearAll() {
        this.handlers.clear();
    }

    public synchronized void removeHandler(String handlerKey) {
        this.handlers.remove(handlerKey);
    }

    public Object findHandler(String key) throws InstantiationException, IllegalAccessException {
        Object handler = getHandlerInstance(key);
        if (handler == null) {
            int firstDotPos = key.indexOf(DOT_SEPARATOR);
            if (firstDotPos > 0) {
                key = key.substring(0, firstDotPos);
            }
            handler = getHandlerInstance(key);
            if ((handler != null) && (!handler.getClass().isAnnotationPresent(MultiHandler.class))) {
                handler = null;
            }
        }
        return handler;
    }

    private Object getHandlerInstance(String key) throws InstantiationException, IllegalAccessException {
        Class<?> handlerClass = this.handlers.get(key);
        if (handlerClass == null) {
            return null;
        }
        return handlerClass.newInstance();
    }

    public void addParseClass(String handlerKey, Class<?> clazz) {
        parseClasses.put(handlerKey, clazz);
    }

    public Object parse2PBMsg(String key, byte[] data, int len) throws Exception {
        Class<?> clazz = parseClasses.get(key);
        Method newBuilder = clazz.getMethod("newBuilder", new Class[]{});
        Object o = newBuilder.invoke(null, new Object[]{});
        Method merageFrom = o.getClass().getMethod("mergeFrom", new Class[]{byte[].class, int.class, int.class});
        o = merageFrom.invoke(o, new Object[]{data, 0, len});
        Method builder = o.getClass().getMethod("build", new Class[]{});
        return builder.invoke(o, new Object[]{});
    }
}