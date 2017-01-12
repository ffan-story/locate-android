package com.feifan.locatelib.network;

import android.text.TextUtils;
import android.util.Base64;

import com.feifan.baselib.utils.LogUtils;

import java.io.UnsupportedEncodingException;
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

    /**
     * 获取
     * @param params
     * @return
     */
    public static String computeSignValue(Map<String, String> params) {
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

    /**
     * Base64编码
     * <p>
     *     example:
     *     UrlUtils.encodeToBase64("[[\"A3FCE438-627C-42B7-AB72-DC6E55E137AC\",\"11000\"],[\"A3FCE438-627C-42B7-AB72-DC6E55E137AC\",\"21000\"]]");
     *     Base64 code : W1siQTNGQ0U0MzgtNjI3Qy00MkI3LUFCNzItREM2RTU1RTEzN0FDIiwiMTEwMDAiXSxbIkEzRkNFNDM4LTYyN0MtNDJCNy1BQjcyLURDNkU1NUUxMzdBQyIsIjIxMDAwIl1d
     * </p>
     * @param content
     * @return
     */
    public static String encodeToBase64(String content) {
        String result = "";
        if(!TextUtils.isEmpty(content)) {
            try {
                result = Base64.encodeToString(content.getBytes("utf-8"), Base64.DEFAULT);
                LogUtils.d(content + " base64 to " + result);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return result;
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
