package com.feifan.sampling.util;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.feifan.sampling.Constants;
import com.libs.utils.PrefUtil;
import com.wanda.logger.log.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * ContentProvider工具类
 *
 * Created by xuchunlei on 16/4/25.
 */
public class ProviderUtil {

    private static HandlerThread sWorkerThread = new HandlerThread("data-writer");
    static {
        sWorkerThread.start();
    }
    private static Handler sWorker = new Handler(sWorkerThread.getLooper());

    private ProviderUtil(){

    }

    /**
     * 在工作线程中执行操作
     * @param r
     */
    public static void runOnWorkerThread(Runnable r) {
        if(sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            sWorker.post(r);
        }
    }

    /**
     * 将数据导出为csv文件
     * @param cursor
     */
    public static File exportToCSV(Cursor cursor, Context context,String fileName) {
        int rowCount = 0;
        int colCount = 0;
        FileWriter fw;
        BufferedWriter bfw;
        String path = PrefUtil.getString(context,Constants.SHAREPREFERENCE.LOG_FILE_PATH,Constants.SHAREPREFERENCE.DEFAULT_LOG_FILE_PATH);
        File filePath = new File(Constants.FILE_PATH_EXPORT_SAMPLE_DATA.concat(File.separator).concat(path).concat(File.separator));
        if(!filePath.exists()){
            if (!filePath.mkdirs()){//创建文件夹
                return null;
            }
        }
        File saveFile = new File(Constants.FILE_PATH_EXPORT_SAMPLE_DATA.concat(File.separator).concat(File.separator).concat(path).concat(File.separator).concat(fileName));
        try {
            rowCount = cursor.getCount();
            colCount = cursor.getColumnCount();
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);
            if (rowCount > 0) {
                cursor.moveToFirst();
                // 写入表头
                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1)
                        bfw.write(cursor.getColumnName(i) + ',');
                    else
                        bfw.write(cursor.getColumnName(i));
                }
                // 写好表头后换行
                bfw.newLine();
                // 写入数据
                for (int i = 0; i < rowCount; i++) {
                    cursor.moveToPosition(i);
                    Log.v("导出数据", "正在导出第" + (i + 1) + "条");
                    for (int j = 0; j < colCount; j++) {
                        if (j != colCount - 1)
                            bfw.write(cursor.getString(j) + ',');
                        else
                            bfw.write(cursor.getString(j));
                    }
                    // 写好每条记录后换行
                    bfw.newLine();
                }
            }
            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
//            Toast.makeText(this, "导出完毕！", Toast.LENGTH_SHORT).show();
            Log.v("导出数据", "导出完毕！");
            return saveFile;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
//            c.close();
        }
        return null;
    }

    /**
     * 将数据导出为csv文件
     * @param cursor
     */
    public static void exportToFile(Cursor cursor) {
        int rowCount = 0;
        int colCount = 0;
        try {
            rowCount = cursor.getCount();
            colCount = cursor.getColumnCount();
            if (rowCount > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < rowCount; i++) {
                    cursor.moveToPosition(i);
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < colCount; j++) {
                        sb.append(cursor.getString(j)+"  ");
                    }
                    if(!TextUtils.isEmpty(sb.toString())){
                        Logger.writeFile(sb.toString());
                    }
                }
            }
        } finally {
            cursor.close();
        }
    }
}
