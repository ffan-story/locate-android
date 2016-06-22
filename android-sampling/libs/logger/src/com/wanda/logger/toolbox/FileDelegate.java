package com.wanda.logger.toolbox;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.wanda.logger.base.DefaultConfig;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by mengmeng on 15/6/9.
 */
public class FileDelegate {

    private final String FILE_NAME = "log.txt";
    private final String FILE_DECTORY = Environment.getExternalStorageDirectory().getAbsolutePath()+"/indoor/";
    private static FileDelegate mFileDelegate;
    private File mLogFile;
    private IConfig mConfig;

    private FileDelegate(IConfig config) {
        if(config == null){
            config = new DefaultConfig();
        }
        mConfig = config;
        initFile();
    }

    private void initFile() {
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mConfig.getFilePath()+"/");
        mLogFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mConfig.getFilePath()+"/"+ FILE_NAME);
        if (!path.exists()) {
            Log.d("TestFile", "Create the path:" + path.getAbsolutePath());
            path.mkdir();
        }
        if (!mLogFile.exists()) {
            try {
                mLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setConfig(IConfig config ){
        mConfig = config;
        initFile();
    }

    public synchronized static FileDelegate getInstance(IConfig config) {
        if (mFileDelegate == null) {
            mFileDelegate = new FileDelegate(config);
        }
        return mFileDelegate;
    }

    public void writeLogFile(String log){
        if(!TextUtils.isEmpty(log)){
            write2File(log);
        }
    }

    public void writeCustomFile(String filepath,String filename,String content){
        if(!TextUtils.isEmpty(filename) && TextUtils.isEmpty(filepath) && !TextUtils.isEmpty(content)){
            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+filepath+"/");
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+filepath+"/" + filename);
            if (!path.exists()) {
                Log.d("TestFile", "Create the path:" + path.getAbsolutePath());
                path.mkdir();
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            write2File(file,content);
        }
    }

    public String getCustomFileStr(String filepath,String filename){
        String fileUrl;
        if(!TextUtils.isEmpty(filepath) && !TextUtils.isEmpty(filename)){
            if(filepath.endsWith(File.pathSeparator)){
                fileUrl = filepath + filename;
            }else {
                fileUrl = filepath + File.separator + filename;
            }
            try {
                return readFileSdcardFile(fileUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public boolean isFileExist(String filepath,String filename){
        if(!TextUtils.isEmpty(filename)){

            File path;
            if(!TextUtils.isEmpty(filepath)) {
                path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filepath + "/");
            }else{
                path = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            }
            File file = new File(path.getPath()+"/" + filename);
            if (!path.exists()) {
                Log.d("TestFile", "Create the path:" + path.getAbsolutePath());
                return false;
            }
            if (!file.exists()) {
                return false;
            }
            return true;
        }
        return false;
    }
    /**
     * write log to file
     * @param log
     */
    private void write2File(String log) {
       write2File(mLogFile,log);
    }
    /**
     * write log to file
     * @param log
     */
    private synchronized void write2File(File file, String log) {
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+mConfig.getFilePath());
        if (!path.exists()) {
            initFile();
        }
        if (!TextUtils.isEmpty(log)) {
            try {
                FileOutputStream outputStream = new FileOutputStream(file, true);
                StringBuffer sb=new StringBuffer();
                sb.append(log);
                outputStream.write(sb.toString().getBytes("utf-8"));
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //写数据到SD中的文件
    public void writeFileSdcardFile(String fileName, String write_str) throws IOException {
        try {

            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = write_str.getBytes();

            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //读SD中的文件
    public String readFileSdcardFile(String fileName) throws IOException {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];
            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * 将数据导出为csv文件
     */
    public static File exportToCSV( Context context, String fileName,String[] titles,String[] args) {
        int rowCount = 0;
        int colCount = 0;
        FileWriter fw;
        BufferedWriter bfw;
//        String path = PrefUtil.getString(context,Constants.SHAREPREFERENCE.LOG_FILE_PATH,Constants.SHAREPREFERENCE.DEFAULT_LOG_FILE_PATH);
//        File filePath = new File(Constants.FILE_PATH_EXPORT_SAMPLE_DATA.concat(File.separator).concat(path).concat(File.separator));
//        if(!filePath.exists()){
//            if (!filePath.mkdirs()){//创建文件夹
//                return null;
//            }
//        }
//        File saveFile = new File(Constants.FILE_PATH_EXPORT_SAMPLE_DATA.concat(File.separator).concat(File.separator).concat(path).concat(File.separator).concat(fileName));
//        try {
//            rowCount = cursor.getCount();
//            colCount = cursor.getColumnCount();
//            fw = new FileWriter(saveFile);
//            bfw = new BufferedWriter(fw);
//            if (rowCount > 0) {
//                cursor.moveToFirst();
//                // 写入表头
//                for (int i = 0; i < colCount; i++) {
//                    if (i != colCount - 1)
//                        bfw.write(cursor.getColumnName(i) + ',');
//                    else
//                        bfw.write(cursor.getColumnName(i));
//                }
//                // 写好表头后换行
//                bfw.newLine();
//                // 写入数据
//                for (int i = 0; i < rowCount; i++) {
//                    cursor.moveToPosition(i);
//                    Log.v("导出数据", "正在导出第" + (i + 1) + "条");
//                    for (int j = 0; j < colCount; j++) {
//                        if (j != colCount - 1)
//                            bfw.write(cursor.getString(j) + ',');
//                        else
//                            bfw.write(cursor.getString(j));
//                    }
//                    // 写好每条记录后换行
//                    bfw.newLine();
//                }
//            }
//            // 将缓存数据写入文件
//            bfw.flush();
//            // 释放缓存
//            bfw.close();
////            Toast.makeText(this, "导出完毕！", Toast.LENGTH_SHORT).show();
//            Log.v("导出数据", "导出完毕！");
//            return saveFile;
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } finally {
////            c.close();
//        }
        return null;
    }

}
