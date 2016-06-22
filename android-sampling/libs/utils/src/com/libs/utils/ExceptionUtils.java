package com.libs.utils;

/**
 * Created by mengmeng on 16/5/16.
 */
public class ExceptionUtils {
    public static void throwArgumentExeception(String argumentStr) {
        throw new IllegalArgumentException(argumentStr);
    }

}
