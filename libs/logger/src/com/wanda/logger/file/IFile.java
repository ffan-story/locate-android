package com.wanda.logger.file;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.wanda.logger.base.DefaultConfig;
import com.wanda.logger.toolbox.IConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by mengmeng on 16/6/7.
 */
public abstract class IFile {
    public abstract void init();

    protected File mLogFile;
    private IConfig mConfig;
    public IFile(IConfig config){
        if (config == null){
            mConfig = new DefaultConfig();
        }
        initFile();
    }

    public void initFile(){
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mConfig.getFilePath()+"/");
       if (!path.exists()) {
            Log.d("TestFile", "Create the path:" + path.getAbsolutePath());
            path.mkdir();
        }
        String fileName = mConfig.getFileName()+mConfig.getPostFix();
        if(TextUtils.isEmpty(fileName) || !fileName.contains(".")){
            throw new IllegalArgumentException("the filename can not be null or filename format illegal");
        }
        mLogFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mConfig.getFilePath()+"/"+ fileName);
        if (!mLogFile.exists()) {
            try {
                mLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param log
     */
    public void write2File(String log){

    }

    public void write2File(List<String[]> list){

    }
}
