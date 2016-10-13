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

    public static final String EXTRA_KEY_BUILDING = "building";

    /**
     * 导出文件列-普通采集
     */
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

//    public static final String[] EXPORT_FILE_TITLES_LI

    // export
    public static final String EXPORT_ROOT_PATH_NAME;    // 根目录
    public static String EXPORT_PARENT_PATH_NAME;        // 父目录

    static {
        EXPORT_ROOT_PATH_NAME = Environment.getExternalStorageDirectory().getAbsolutePath().
                concat(File.separator).concat("locate").concat(File.separator);
    }

    public static void setExportParentPathName(String parentPathName) {
        EXPORT_PARENT_PATH_NAME = parentPathName.concat(File.separator);
        createDirectory(getExportFilePath());
    }

    public static String getExportFilePath() {
        return EXPORT_ROOT_PATH_NAME.concat(EXPORT_PARENT_PATH_NAME);
    }

    private static void createDirectory(String dirName) {
        File path = new File(dirName);
        if(!path.exists()) {
            if (path.mkdirs()){//创建文件夹
                LogUtils.i("create " + dirName);
            }else {
                LogUtils.e("failed to create " + dirName);
            }
        }
    }

    private Constants() {

    }
}
