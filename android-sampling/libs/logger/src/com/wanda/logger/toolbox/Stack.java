package com.wanda.logger.toolbox;

/**
 * Created by mengmeng on 15/6/9.
 */
public abstract class Stack {
    protected IConfig mConfig;
    private enum STACK_TYPE{NET,FILE};

    private STACK_TYPE mType = STACK_TYPE.FILE;

    public abstract int performFileLog(String log);

    public abstract int performNetLog(String log);

    public void setStackType(STACK_TYPE type){
        mType = type;
    }

    public STACK_TYPE getStackType(){
        return mType;
    }

    public boolean isFileStack(){
        return mType == STACK_TYPE.FILE;
    }

    public abstract String getSaveStr();
    public abstract void saveStr();

}
