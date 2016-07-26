package com.libs.base.mvp;

import java.lang.reflect.Method;

/**
 * Created by mengmeng on 16/5/26.
 */
public class MvpMethord {
    private String eventName;

    public EventType getEventType() {
        return this.eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    private EventType eventType;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    private Method method;
    private String methordName;
    private Class[] parameterType;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getMethordName() {
        return methordName;
    }

    public void setMethordName(String methordName) {
        this.methordName = methordName;
    }

    public Class[] getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class[] parameterType) {
        this.parameterType = parameterType;
    }
}
