package com.feifan.locate;

import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;

import com.feifan.baselib.utils.LogUtils;

import java.io.File;

/**
 * 常量
 *
 * Created by bianying on 16/9/4.
 */
public class Constants {

    /**
     * 默认采样次数
     */
    public static final int SCAN_DEFAULT_TIMES = 100;

    public static final String[] EXPORT_FILE_TITLES = new String[]{
            "UUID",
            "major",
            "minor",
            "rssi",
            "accuracy",
            "direction",
            "datetime",
            "loc_x",
            "loc_y",
            "loc_d",
            "floor"
    };

    public static final String EXPORT_PATH_NAME;

    static {
        EXPORT_PATH_NAME = Environment.getExternalStorageDirectory().getAbsolutePath().
                concat(File.separator).concat("locate").concat(File.separator);
        File path = new File(EXPORT_PATH_NAME);
        if(!path.exists()) {
            if (path.mkdirs()){//创建文件夹
                LogUtils.i("create " + EXPORT_PATH_NAME);
            }else {
                LogUtils.e("failed to create " + EXPORT_PATH_NAME);
            }
        }
    }

    private Constants() {

    }
}
