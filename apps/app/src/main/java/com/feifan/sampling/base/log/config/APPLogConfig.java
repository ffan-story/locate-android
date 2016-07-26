package com.feifan.sampling.base.log.config;

import android.text.TextUtils;

import com.feifan.sampling.Constants;
import com.wanda.logger.toolbox.IConfig;

/**
 * Created by mengmeng on 16/6/16.
 */
public class APPLogConfig extends IConfig{
    private String mFilename;
    private String mFilePath;

    public APPLogConfig(String filename){
        this(filename,"");
    }

    public APPLogConfig(String filename,String filepath){
        if(!TextUtils.isEmpty(filename)){
            mFilename = filename.trim();
        }
        if(!TextUtils.isEmpty(filepath)){
            mFilePath = filepath.trim();
        }else {
            mFilePath = Constants.SHAREPREFERENCE.DEFAULT_LOG_FILE_PATH;
        }
    }
    @Override
    public String getFilePath() {
        return mFilePath;
    }

    @Override
    public String getFileName() {
        return mFilename;
    }

    @Override
    public String getPostFix() {
        return ".csv";
    }
}
