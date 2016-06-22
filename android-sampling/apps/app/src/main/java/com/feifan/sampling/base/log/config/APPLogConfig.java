package com.feifan.sampling.base.log.config;

import android.text.TextUtils;

import com.wanda.logger.toolbox.IConfig;

/**
 * Created by mengmeng on 16/6/16.
 */
public class APPLogConfig extends IConfig{
    private String mFilename;
    public APPLogConfig(String filename){
        if(!TextUtils.isEmpty(filename)){
            mFilename = filename.trim();
        }
    }
    @Override
    public String getFilePath() {
        return "indoor";
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
