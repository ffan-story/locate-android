package com.wanda.logger.toolbox;

import android.content.Context;

import java.io.File;

/**
 * Created by mengmeng on 15/6/9.
 */
public class PerformanceDelegate {

    private final String FILE_DIRECTORY_NAME = "indoor";
    private final String PERFORMANCE_NAME = "performance";
    private String mPerformanceStr;
    private String mSaveFilePath;
    private Context mContext;

    public PerformanceDelegate(Context context,String performanceStr) {
        mContext = context;
        mPerformanceStr = performanceStr;
        initSavePath();
    }

    private void initSavePath() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            mSaveFilePath = android.os.Environment.getExternalStorageDirectory()
                    + File.separator + FILE_DIRECTORY_NAME + File.separator
                    + FILE_DIRECTORY_NAME + File.separator + LogUtil.getNowTime() + ".csv";
        } else {
            mSaveFilePath = mContext.getFilesDir().getPath()
                    + File.separator + FILE_DIRECTORY_NAME + File.separator + FILE_DIRECTORY_NAME
                    + File.separator + LogUtil.getNowTime() + ".csv";
        }
    }
}
