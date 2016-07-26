package com.libs.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.GeomagneticField;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by mengmeng on 16/5/16.
 */
public class HardWareUtils {
    /**
     * 获得当前总内存大小
     */
    public static String getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 是否是模拟器
     * @param context
     * @return
     */
    public static boolean isEmulator(Context context) {
        String imei = getIMEI(context);
        if ("000000000000000".equals(imei)) {
            return true;
        }
        return (Build.MODEL.equalsIgnoreCase("sdk")) || (Build.MODEL.equalsIgnoreCase("google_sdk")) || Build.BRAND.equalsIgnoreCase("generic");
    }


    private static String getIMEI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            return imei;
        } catch (Exception ioe) {
        }
        return null;
    }


    /**
     * 判断是否为平板设备
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        //暂时屏蔽平板
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
//    		return (context.getResources().getConfiguration().screenLayout
//                    & Configuration.SCREENLAYOUT_SIZE_MASK)
//                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        }
        return false;
    }


    /**
     * 系统为Android2.1.x
     * @return
     */
    public static boolean isEclair_MR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1;
    }

    /**
     * 判断SD卡是否插入 即是否有SD卡
     */
    public static boolean isSDCardMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 是否：已经挂载,但只拥有可读权限
     */
    public static boolean isSDCardMountedReadOnly() {
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 获取android当前可用内存大小
     */
    public static String getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存

        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    }

    /**
     * 获取屏幕的亮度
     */
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 获取手机屏幕的宽和高
     * @param c
     * @return map("w", width) map("h",height);
     */
    public static HashMap<String, Integer> getWidth_Height(Context c) {
        DisplayMetrics metrics = c.getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        HashMap<String, Integer> m = new HashMap<String, Integer>();
        m.put("w", width);
        m.put("h", height);
        return m;
    }

    /**
     * 获取平板在横屏时webview的宽度
     *
     * @param c
     * @return
     */
    public static int getTabletWebViewWidth(FragmentActivity c) {
        // 0.82f根据当前webview的padding计算得来
        return (int) ((float) getScreenWidth(c) * 0.82f / c.getResources().getDisplayMetrics().density);
    }

    /**
     * 获取手机屏幕的宽和高size wxh
     * @param c
     * @return width X height
     */
    public static String getWidthXHeight(Context c) {
        Map<String, Integer> m = getWidth_Height(c);
        String size = m.get("w") + "x" + m.get("h");
        return size;
    }

    /**
     * 获取手机分辨率宽度大小
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(FragmentActivity context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    /**
     * 获取手机分辨率长度大小
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(FragmentActivity context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    /**
     * 获得当前的cup是几核的
     *
     * @Title: getNumCores
     * @Description: TODO
     * @param @return
     * @return int
     * @throws
     */
    public static int getNumCores() {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                // Check if filename is "cpu", followed by a single digit number
                return Pattern.matches("cpu[0-9]", pathname.getName());
            }
        }

        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            Log.d("getNumCores", "CPU Count: " + files.length);
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            // Print exception
            Log.d("getNumCores", "CPU Count: Failed.");
            e.printStackTrace();
            // Default to return 1 core
            return 1;
        }
    }


    /**
     * is support front camera
     * @param context
     * @return
     */
    public static boolean isSupportFrontCamare(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    /*
 * Generate a new OpenUDID
 */
    public static String generateOpenUDID(Context context) {
        // Try to get the ANDROID_ID
        String openUDID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (openUDID == null || openUDID.equals("9774d56d682e549c") || openUDID.length() < 15) {
            // if ANDROID_ID is null, or it's equals to the GalaxyTab generic ANDROID_ID or bad, generates
            // a new one
            final SecureRandom random = new SecureRandom();
            openUDID = new BigInteger(64, random).toString(16);
        }
        return openUDID;
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getUUID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        Log.d("debug", "uuid=" + uniqueId);
        return uniqueId;
    }

    public String getGeoNorthDeclination(Context context) {
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) context.getSystemService(serviceName);
        String providerName = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        Location loc = locationManager.getLastKnownLocation(providerName);
        //如果我们开启了gps，通常优选为gps，但是室内实际上很难马上获得gps，我们可以通过位置改变监听器的方式获取，为了方便，本例我们将改用network的基站三角定位的方式获得。
        if(loc == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            //在室内，由于不容易搜索到GPS，建议采用network方式。请注意，有些设备要在配置那里打开网络定位的选项，否则，network方式不能enabled，不能有效使用网络方式，getLastKnownLocation()仍会为null。正规的应用发现disabled，应该询问用户，并通过intent打开相关的配置页。
            loc = locationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
        }

        if(loc == null)
            return null;

        GeomagneticField geo = new GeomagneticField((float)loc.getLatitude(),(float)loc.getLongitude(),
                (float)loc.getAltitude(),System.currentTimeMillis());
        float declination = geo.getDeclination();
        return String.format("磁偏角：%7.3f", declination);
    }
}
