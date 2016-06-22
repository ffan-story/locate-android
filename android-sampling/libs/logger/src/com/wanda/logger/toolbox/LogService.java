package com.wanda.logger.toolbox;

/**
 * Created by mengmeng on 15/6/10.
 */
public class LogService {

    private LogQueue mLogQueue;

    public LogService(){
        mLogQueue = LogQueue.getInstance();
        mLogQueue.start();
    }

    public void sendLogMsg(Request request){
        if(request != null){
            mLogQueue.addLog(request);
        }
    }


}
