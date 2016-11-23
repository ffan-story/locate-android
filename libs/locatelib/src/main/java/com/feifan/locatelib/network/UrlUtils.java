package com.feifan.locatelib.network;

import com.feifan.baselib.utils.LogUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by xuchunlei on 2016/11/17.
 */

public class UrlUtils {
    private UrlUtils() {

    }

    public static String computeSingValue(Map<String, String> params) {
        List<String> paramCache = new ArrayList<>();
        for(Map.Entry<String, String> param : params.entrySet()) {
            paramCache.add(param.getKey() + "=" + param.getValue());
        }
        Collections.sort(paramCache);
        String queryContent = "";
        for(String query : paramCache) {
            queryContent += query + "&";
        }
        queryContent = queryContent + "callSecret=A9F3948C48481EF35F346790CA136C9A";
        LogUtils.e(queryContent);
        try {
            return getSHA(queryContent);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getSHA(String val) throws NoSuchAlgorithmException{
        MessageDigest md5 = MessageDigest.getInstance("SHA-1");
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return byte2hex(m);
    }

    //二进制转字符串
    private static String byte2hex(byte[] b)
    {
        String hs="";
        String stmp="";
        for (int n=0;n<b.length;n++)
        {
            stmp=(Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1) {
                hs= hs + "0" + stmp;
            }
            else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

}
