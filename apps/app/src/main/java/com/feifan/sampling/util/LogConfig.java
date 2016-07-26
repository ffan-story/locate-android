package com.feifan.sampling.util;

import android.content.Context;

import com.feifan.sampling.Constants;
import com.libs.utils.PrefUtil;
import com.wanda.logger.toolbox.IConfig;

/**
 * Created by mengmeng on 16/5/25.
 */
public class LogConfig extends IConfig {
    private Context mContext;

    public LogConfig(Context context){
        mContext = context;
    }

    @Override
    public String getFilePath() {
        String path = PrefUtil.getString(mContext, Constants.SHAREPREFERENCE.LOG_FILE_PATH,"indoor");
        return path;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public String getPostFix() {
        return null;
    }
}
