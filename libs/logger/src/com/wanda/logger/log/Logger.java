package com.wanda.logger.log;

import android.content.Context;

import com.wanda.logger.toolbox.Request;

/**
 * Created by mengmeng on 15/6/10.
 */
public class Logger {

    private static ILogerImpl mLogImpl;
    public static void init(String tag, Context context){
        if(mLogImpl == null) {
            mLogImpl = ILogerImpl.getInstance(tag, context);
            mLogImpl.printPhoneInfo();
        }
    }

    public static void d(String msg) {
        mLogImpl.d(msg);
    }

    public static void e(String msg) {
        mLogImpl.e(msg);
    }

    public static void e(Context context,String msg) {
        if(mLogImpl == null){
           init("",context.getApplicationContext());
        }
        mLogImpl.e(msg);
    }

    public static void i(String msg) {
        mLogImpl.i(msg);
    }

    public static void w(String msg) {
        mLogImpl.w(msg);
    }
    public static void json(String msg) {
        mLogImpl.json(msg);
    }
    public static void xml(String msg) {
        mLogImpl.xml(msg);
    }

    public static void writeFile(String msg){
        mLogImpl.writeFile(msg);
    }

    public static void writeRequest(Request request){
        mLogImpl.writeFile(request);
    }

    public static void d(String tag, String msg) {
        mLogImpl.d(tag,msg);
    }

    public static void e(String tag, String msg) {
        mLogImpl.e(tag,msg);
    }

    public static void i(String tag, String msg) {
        mLogImpl.i(tag,msg);
    }

    public static void w(String tag, String msg) {
        mLogImpl.w(tag,msg);
    }
}
