package com.libs.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by mengmeng on 16/5/26.
 */
public class DateTimeUtils {
    /**
     * 将日期字符串从sformat格式转换为dformat格式
     * @param adateStrteStr
     * @param sformat  adateStrteStr的日期格式
     * @param dformat  返回字符串的日期格式
     * @return
     */
    public static String convertDate(String adateStrteStr, String sformat, String dformat) {
        String date = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sformat);
            Date d = simpleDateFormat.parse(adateStrteStr);
            simpleDateFormat=new SimpleDateFormat(dformat);
            date = simpleDateFormat.format(d);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return date;
    }

    /**
     * 获取前后任一天数的日期
     * @param count
     * @return
     */
    public static String returnDate(int count) {
        Calendar strDate = Calendar.getInstance();
        strDate.add(Calendar.DATE, count);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(strDate.getTime());
    }


    /**
     * 获取指定时间
     * @return
     */
    public static String formatTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        SimpleDateFormat sformat = new SimpleDateFormat("HH:mm");

        return sformat.format(calendar.getTime());
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 显示为12小时格式
        SimpleDateFormat simpleDF = new SimpleDateFormat("HH:mm");
        String logTime = simpleDF.format(calendar.getTime());
        return logTime;
    }

    /**
     * 获取当前时间
     * @param format yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getCurrentTime(String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 显示为12小时格式
        SimpleDateFormat simpleDF = new SimpleDateFormat(format);
        String logTime = simpleDF.format(calendar.getTime());
        return logTime;
    }


    /**
     * 判断是不是合法时间
     * @return
     */
    public static boolean isValidTime(String startTime, String endTime) {
        return isValidTime("yyyy-MM-dd HH:mm:ss", "Asia/Shanghai", startTime, endTime);
    }

    /**
     * 判断是不是合法时间
     * @return
     */
    public static boolean isValidTime(String format, String timeZone, String startTime, String endTime) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            TimeZone timeZoneshanghai = TimeZone.getTimeZone(timeZone);
            df.setTimeZone(timeZoneshanghai);

            Date startDate = df.parse(startTime);
            Date endDate = df.parse(endTime);
            long start = startDate.getTime();
            long end = endDate.getTime();

            long now = System.currentTimeMillis();
            if(now > start && now < end){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
