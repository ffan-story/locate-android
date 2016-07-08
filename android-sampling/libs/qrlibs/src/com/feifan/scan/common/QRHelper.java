package com.feifan.scan.common;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2016/1/22.
 */
public class QRHelper {
    public static final int DEFAULT_FORGROUND = 0xffffffff;
    public static final int DEFAULT_BACKGROUND = 0xff000000;
    public static final String DEFAULT_ENCODE = "UTF-8";
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        return Bitmap2Bytes(bm, 100);
    }

    public static byte[] Bitmap2Bytes(Bitmap bm, int quality) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
            return baos.toByteArray();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            quality = quality / 2;
            return Bitmap2Bytes(bm, quality);
        } finally {
            if (bm != null) {
                bm.recycle();
            }
        }
    }

    public static boolean isNumeric(String str){
        for (int i = 0; i < str.length(); i++){
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }
}
