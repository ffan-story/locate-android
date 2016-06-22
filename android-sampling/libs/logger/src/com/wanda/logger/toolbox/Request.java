package com.wanda.logger.toolbox;

/**
 * Created by mengmeng on 15/6/9.
 */
public abstract  class Request<T> implements Comparable<Request<T>> {

    protected enum REQUEST_TYPE{NET,FILE,PERFORMANCE};

    public int priority = 1;

    private T mLog;
    public T getLog() {
        return mLog;
    }

    public void setLog(T log) {
        this.mLog = log;
    }

    public abstract void writeLog(T log);

    protected IConfig mConfig;

    public IConfig getmConfig() {
        return mConfig;
    }

    public void setmConfig(IConfig mConfig) {
        this.mConfig = mConfig;
    }

    public Request(IConfig config){
        if(config != null) {
            mConfig = config;
        }else {
            throw new IllegalArgumentException("the config can not be null");
        }
    }
}
