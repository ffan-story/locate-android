package com.feifan.locate.utils;

import android.graphics.BitmapFactory;

import com.feifan.baselib.utils.LogUtils;

import java.io.InputStream;

/**
 * 图片工具类
 * Created by xuchunlei on 16/9/21.
 */
public class ImageUtils {

    private ImageUtils() {

    }

    public static BitmapFactory.Options getOptionsFromStream(InputStream is) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        LogUtils.d("image's real size:image.Height=" + options.outHeight + ",image.Width=" + options.outWidth);
        return options;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
