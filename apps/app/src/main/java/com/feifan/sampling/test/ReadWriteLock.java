package com.feifan.sampling.test;

/**
 * Created by mengmeng on 16/5/27.
 */
public class ReadWriteLock {
    private int reads = 0;
    private int writeAccess = 0;
    private int writeRequests = 0;
    public synchronized void lockReaded()throws InterruptedException{
        if(isWriter()){
            wait();
        }
        reads ++;
    }

    private boolean isWriter(){
        if(writeAccess >0 || writeRequests >0){
            return true;
        }
        return false;
    }
}
