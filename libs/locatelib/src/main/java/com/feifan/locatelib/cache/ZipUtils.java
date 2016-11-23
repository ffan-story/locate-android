package com.feifan.locatelib.cache;

import com.feifan.baselib.utils.IOUtils;
import com.feifan.baselib.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by xuchunlei on 2016/11/11.
 */

public class ZipUtils {

    private ZipUtils() {

    }

    /**
     * 解压缩
     * @param zipFile
     * @return 解压文件集合
     */
    public static List<String> unZip(String zipFile) {

        List<String> outputFiles = new ArrayList<>();

        File zip = new File(zipFile);
        if(!zip.exists()) {
            LogUtils.w(zipFile + " do not exists.");
            return outputFiles;
        }

        // 解压目录
        String outputDirName = getDefaultUnZipDir(zipFile);
        File outputDir = new File(outputDirName);
        if(!outputDir.exists()) {
            outputDir.mkdirs();
        }

        ZipEntry entry;
        InputStream is = null;
        ZipInputStream zis = null;
        try {
            is = new FileInputStream(zipFile);
            zis = new ZipInputStream(new BufferedInputStream(is));

            byte[] buffer = new byte[1024];
            while((entry = zis.getNextEntry()) != null) {
                String outputFile = outputDirName.concat(entry.getName());
                if(!isUnzipped(outputFile)) {
                    FileOutputStream fos = null;
                    try{
                        // 保存文件
                        fos = new FileOutputStream(outputFile);
                        int count;
                        while ((count = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, count);
                        }
                        LogUtils.i("save fingerprint file to " + outputFile);
                    } finally {
                        IOUtils.closeQuietly(fos);
                    }
                }
                LogUtils.i("fingerprint file is existed:" + outputFile);
                outputFiles.add(outputFile);
                zis.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(zis);
        }
        return outputFiles;
    }

    public static String getDefaultUnZipDir(String fileName) {
        String dirName = getZipName(fileName);
        int pathIndex = fileName.lastIndexOf("/");
        return fileName.substring(0, pathIndex + 1).concat(dirName).concat(File.separator);
    }

    private static boolean isUnzipped(String fileName) {
        File f = new File(fileName);
        return f.exists();
    }

    // 获取文件名（不包括扩展名）
    private static String getZipName(String fullName) {
        int index = fullName.lastIndexOf("/");
        String lastName = fullName;
        if(index != -1) {
            lastName = fullName.substring(index + 1, fullName.length() - 1);
        }

        if ((lastName != null) && (lastName.length() > 0)) {
            int dot = lastName.lastIndexOf('.');
            if ((dot >-1) && (dot < (lastName.length()))) {
                return lastName.substring(0, dot);
            }
        }
        return lastName;
    }

}
