package com.feifan.baselib.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by xuchunlei on 15/10/14.
 */
public class IOUtils {
    private IOUtils(){

    }

    /**
     * 关闭IO资源
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
}
