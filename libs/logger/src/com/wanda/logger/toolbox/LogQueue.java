package com.wanda.logger.toolbox;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by mengmeng on 15/6/9.
 */
public class LogQueue {

    /** The queue of requests to service. */
    private BlockingQueue<Request> mQueue = new PriorityBlockingQueue<Request>();
    private LogDispatcher mDispatcher;
    private static LogQueue mLogQueue;

    public LogQueue(){
    }

    public static LogQueue getInstance(){
        if(mLogQueue == null){
            mLogQueue = new LogQueue();
        }
        return mLogQueue;
    }

    public void start(){
        if(mDispatcher == null) {
            mDispatcher = new LogDispatcher(mQueue);
        }
        if(!mDispatcher.isAlive()) {
            mDispatcher.start();
        }
    }
    /**
     *
     * @param request
     */
    public void addLog(Request request){
        synchronized (mQueue) {
            if (request != null) {
                mQueue.add(request);
                if (mDispatcher != null && !mDispatcher.isAlive()){
                    mDispatcher.start();
                }
            }
        }
    }

    public void onStop(){
        if(mDispatcher != null){
            mDispatcher.quit();
        }
    }
}
