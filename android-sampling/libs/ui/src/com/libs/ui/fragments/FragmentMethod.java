package com.libs.ui.fragments;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2015/12/30.
 */
public class FragmentMethod {
    final Method method;
    final Class<?> eventType;
//    final Object event;
    /** Used for efficient comparison */
    String methodString;

    FragmentMethod(Method method, Class<?> eventType) {
        this.method = method;
        this.eventType = eventType;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FragmentMethod) {
            checkMethodString();
            FragmentMethod otherSubscriberMethod = (FragmentMethod)other;
            otherSubscriberMethod.checkMethodString();
            return methodString.equals(otherSubscriberMethod.methodString);
        } else {
            return false;
        }
    }

    private synchronized void checkMethodString() {
        if (methodString == null) {
            // Method.toString has more overhead, just take relevant parts of the method
            StringBuilder builder = new StringBuilder(64);
            builder.append(method.getDeclaringClass().getName());
            builder.append('#').append(method.getName());
            builder.append('(').append(eventType.getName());
            methodString = builder.toString();
        }
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }
}
