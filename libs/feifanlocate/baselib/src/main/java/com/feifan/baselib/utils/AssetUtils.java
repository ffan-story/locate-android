package com.feifan.baselib.utils;

import android.content.Context;
import android.widget.RadioButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by xuchunlei on 2016/12/1.
 */

public class AssetUtils {

    public static void readLines(Context context, String name, Transactor transactor) {
        InputStream is = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            // 获取合法的定位键信息
            is = context.getAssets().open(name);
            reader = new InputStreamReader(is);
            bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                transactor.transact(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(is);
        }
    }

    public interface Transactor {

        void transact(String line);
    }
}
