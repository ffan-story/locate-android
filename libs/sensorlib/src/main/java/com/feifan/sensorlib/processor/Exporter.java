package com.feifan.sensorlib.processor;

import com.feifan.baselib.utils.LogUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by xuchunlei on 2016/12/8.
 */

public class Exporter {

    private BufferedWriter bfw;

    public Exporter() {

    }

    public void open(String name) {
        File saveFile = new File(name);
        FileWriter fw;

        try {
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e("open buffer writer failed");
        }
    }

    public void close() {

        try {
            bfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeLine(String line) {

        // 写入表头
//            int colCount = titles.length;
//            for (int i = 0; i < colCount; i++) {
//                bfw.write(i != colCount - 1 ? titles[i] + ',' : titles[i]);
//            }
        // 写好表头后换行
//            bfw.newLine();
        // 写入数据
        try {
            bfw.write(line);
            // 写好每条记录后换行
            bfw.newLine();

            // 将缓存数据写入文件
            bfw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
