package com.wanda.logger.log;

/**
 * Created by mengmeng on 15/6/8.
 */
public interface ILog {


    Settings init(String tag);

    Settings getSettings();

    void d(String tag,String message, Object... args);

    void e(String tag,String message, Object... args);

    void e(String tag,Throwable throwable, String message, Object... args);

    void w(String tag,String message, Object... args);

    void i(String tag,String message, Object... args);

    void v(String tag,String message, Object... args);

    void json(String json);

    void xml(String xml);
}
