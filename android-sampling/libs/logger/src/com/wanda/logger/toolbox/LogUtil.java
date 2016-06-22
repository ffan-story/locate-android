package com.wanda.logger.toolbox;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mengmeng on 15/6/9.
 */
public class LogUtil {

    public static File getAppCacheDir(Context context, String subName) {
        if (!sdAvailible()) {
            return null;
        }
        File sd = Environment.getExternalStorageDirectory();
        File dir = new File(sd, getAppName(context));
        File sub = new File(dir, subName);
        sub.mkdirs();
        return sub;
    }

    public static boolean sdAvailible() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }

    public static String encrypt(String str) {
        // TODO: encrypt data.
        return str;
    }

    public static String buildSystemInfo(Context context) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\n");
        buffer.append("#-------system info-------");
        buffer.append("\n");
        buffer.append("version-name:");
        buffer.append(getVersionName(context));
        buffer.append("\n");
        buffer.append("version-code:");
        buffer.append( getVersionCode(context));
        buffer.append("\n");
        buffer.append("system-version:");
        buffer.append( getSystemVersion(context));
        buffer.append("\n");
        buffer.append("model:");
        buffer.append( getModel(context));
        buffer.append("\n");
        buffer.append("density:");
        buffer.append( getDensity(context));
        buffer.append("\n");
        buffer.append("imei:");
        buffer.append( getIMEI(context));
        buffer.append("\n");
        buffer.append("screen-height:");
        buffer.append( getScreenHeight(context));
        buffer.append("\n");
        buffer.append("screen-width:");
        buffer.append( getScreenWidth(context));
        buffer.append("\n");
        buffer.append("unique-code:");
        buffer.append( getUniqueCode(context));
        buffer.append("\n");
        buffer.append("mobile:");
        buffer.append( getMobile(context));
        buffer.append("\n");
        buffer.append("blue:");
        buffer.append("blue is support: "+isSupportBlue(context)+" blue is open: "+isBlueOPen(context)+" is support ow power consumption： "+isBluetoothAvaliable(context));
        buffer.append("\n");
        buffer.append("imsi:");
        buffer.append( getProvider(context));
        buffer.append("\n");
        buffer.append("isWifi:");
        buffer.append( isWifi(context));
        buffer.append("\n");
        return buffer.toString();
    }

    public static String getUniqueCode(Context context) {
        if (context == null)
            return null;
        String imei = getIMEI(context);
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mUniqueCode = imei + "_" + info.getMacAddress();
        return mUniqueCode;
    }

    public static boolean isWifi(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static String getMobile(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }

    public static String getProvider(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSubscriberId();
    }

    public static final String getIMEI(final Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getDeviceId();
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static String getSystemVersion(Context context) {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getModel(Context context) {
        return android.os.Build.MODEL != null ? android.os.Build.MODEL.replace(
                " ", "") : "unknown";
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pinfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_CONFIGURATIONS);
            return pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return "";
    }

    public static String getAppName(Context context) {
        String appname = "";
            try {
                PackageInfo pinfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(),
                        PackageManager.GET_CONFIGURATIONS);
                String packageName = pinfo.packageName;
                if (!TextUtils.isEmpty(packageName)) {
                    appname = packageName.replaceAll("\\.", "_");
                }
            } catch (PackageManager.NameNotFoundException e) {
            }

        return appname;
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo pinfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_CONFIGURATIONS);
            return pinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return 1;
    }

    /**
     * check if support bluetooth and check the bluetooth is open
     * @param context
     * @return
     */
    public static boolean isBluetoothAvaliable(Context context){
        if(context != null){
            if(SystemUtil.isBlueSupport() && isSupportBlue(context) && isBlueOPen(context)){
                return true;
            }
        }
        return false;
    }

    /**
     *is support bluetooth
     * @param context
     * @return
     */
    public static boolean isSupportBlue(Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * is open bluetooth
     * @param context
     * @return
     */
    public static boolean isBlueOPen(Context context){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager != null){
            BluetoothAdapter adapter = bluetoothManager.getAdapter();
            if(adapter != null){
                return adapter.isEnabled();
            }
        }
        return false;
    }

    public static String getWrapperStr(String s){
        if(!TextUtils.isEmpty(s)){
            return "["+s+"]";
        }
        return s;
    }

    public static String getNowTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

}
