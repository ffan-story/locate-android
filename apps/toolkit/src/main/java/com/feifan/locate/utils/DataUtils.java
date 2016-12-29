package com.feifan.locate.utils;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.Constants;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by bianying on 16/9/4.
 */
public class DataUtils {
    private DataUtils() {

    }

    /**
     * 将数据导出为csv文件
     *
     */
    public static File exportToCSV(String[] titles, List<SampleBeacon> data, String fileName) {
        String fullName = Constants.getExportFilePath().concat(fileName);
        if(data == null || data.size() == 0) {
            return null;
        }
        File saveFile = new File(fullName);
        FileWriter fw;
        BufferedWriter bfw;

        try {
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);

            // 写入表头
            int colCount = titles.length;
            for (int i = 0; i < colCount; i++) {
                bfw.write(i != colCount - 1 ? titles[i] + ',' : titles[i]);
            }
            // 写好表头后换行
            bfw.newLine();
            // 写入数据
            int rowCount = data.size();
            for (int i = 0; i < rowCount; i++) {
                SampleBeacon item = data.get(i);
                LogUtils.v("export " + (i + 1) + " data item(" + item.toFormattedString(titles) + ")");
                bfw.write(item.toFormattedString(titles));
                // 写好每条记录后换行
                bfw.newLine();
            }

            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
            LogUtils.i("export data to " + saveFile.getAbsolutePath());
            return saveFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件或目录
     * @param file
     */
    public static void deleteFile(File file){
        if(file == null) {
            return;
        }
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                deleteFile(f);
            }
            file.delete();
        }
    }
}
