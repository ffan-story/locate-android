package com.wanda.logger.toolbox;

/**
 * Created by mengmeng on 15/6/9.
 */
public class LogStack extends Stack {

    public LogStack(IConfig config){
        mConfig = config;
    }
    @Override
    public int performFileLog(String log) {
        FileDelegate fileDelegate = FileDelegate.getInstance(mConfig);
        fileDelegate.writeLogFile(log);
        return 0;
    }

    @Override
    public int performNetLog(String log) {

        return 0;
    }

    @Override
    public String getSaveStr() {
        return null;
    }

    @Override
    public void saveStr() {

    }
}
