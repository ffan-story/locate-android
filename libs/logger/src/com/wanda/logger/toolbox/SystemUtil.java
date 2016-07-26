package com.wanda.logger.toolbox;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;


public class SystemUtil {

    //低功耗蓝牙最小支持api 18
    public static int MINI_BLUETOOTH_SDK_SUPPORT = 18;
    
    public static String getBrand(){
    	return Build.BRAND;
    }
    
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
	 *  获得支持ACTION_SEND的应用列表 
	 */
	public static  List<ResolveInfo> getShareTargets(Context context,String type){
		Intent intent=new Intent(Intent.ACTION_SEND);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType(type);
		PackageManager pm = context.getPackageManager();
	
		return pm.queryIntentActivities(intent,
		PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
	}
	
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	 
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getVersionCode(Context context){
		PackageManager pManager = context.getPackageManager();
		try {
			PackageInfo packinfo = pManager.getPackageInfo(context.getPackageName(), 0);
			if(null!=packinfo){
				return packinfo.versionCode;
			}else{
				return -1;
			}
		}catch (NameNotFoundException e) {
			return -1;
		}
		
	}
	
	public static String getVersionName(Context context){
		PackageManager pManager = context.getPackageManager();
		try {
			PackageInfo packinfo = pManager.getPackageInfo(context.getPackageName(), 0);
			if(null!=packinfo){
				return packinfo.versionName;
			}else{
				return "";
			}
		}catch (NameNotFoundException e) {
			return "";
		}
		
	}
    
    public static int convertDpToPixel(Context context,float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

	
	
    public static void hideInputMethodAndClearFocus(Context context,View view) {
        if (context == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }
    

    /**
     * 是否是模拟器
     * @param context
     * @return
     */
    public static boolean isEmulator(Context context){
        String imei = getIMEI(context);
        if ("000000000000000".equals(imei)){
            return true;
        }
        return (Build.MODEL.equalsIgnoreCase("sdk")) || (Build.MODEL.equalsIgnoreCase("google_sdk")) || Build.BRAND.equalsIgnoreCase("generic");
    }

    /**
     * 当前是否横屏
     * 
     * @param context
     * @return
     */
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
    
    /**
     * 基于Android3.0的平板
     * @param context
     * @return
     */
    public static boolean isHoneycombTablet(Context context) {
        return isHoneycomb() && isTablet(context);
    }
    
    /**
     * 系统为Android3.0
     * @return
     */
    public static boolean isHoneycomb() {
        // Can use static final constants like HONEYCOMB, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
    
    /**
     * 系统为Android2.1.x
     * @return
     */
    public static boolean isEclair_MR1(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1;
    }
    
    /**
     * 判断是否为平板设备
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        //暂时屏蔽平板
    	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
    		return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
//    		return (context.getResources().getConfiguration().screenLayout
//                    & Configuration.SCREENLAYOUT_SIZE_MASK)
//                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;	
    	}
    	return false;
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
     * 获取设备名称.
     * 
     * @return
     */
    public static String getBuildModel() {
        return Build.MODEL;
    }

    /**
     * 获取设备SDK版本号.
     * 
     * @return
     */
    public static int getBuildVersionSDK() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取设备系统版本号.
     * 
     * @return
     */
    public static String getBuildVersionRelease() {
        return Build.VERSION.RELEASE;
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
        MemoryInfo mi = new MemoryInfo();
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
     * @return map("w",width) map("h",height);
     */
    public static HashMap<String,Integer> getWidth_Height(Context c){
        DisplayMetrics metrics = c.getApplicationContext().getResources().getDisplayMetrics(); 
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        HashMap<String,Integer> m = new HashMap<String,Integer>();
        m.put("w", width);
        m.put("h", height);
        return m;
    }

    /**
     * 得到dimen定义的大小
     * @param context
     * @param dimenId
     * @return
     */
    public static int getDimension(Context context,int dimenId){
        return (int)context.getResources().getDimension(dimenId);
    }
    
    private static String NEWLY_INSTALLED_KEY = "newly_installed_key";
    
    /**
     * 判断应用是否安装
     * @param context
     * @param appName
     * @return
     */
	public static boolean isAppInstalled(Context context, String appName) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(appName, 0);
			if (null != packageInfo) {
				return true;
			}
		} catch (NameNotFoundException e) {
		}
		return false;
	}


    /**
     * 返回应用版本号
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        return getAppVersion(context, context.getPackageName());
    }

    /**
     * 返回应用版本号
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        return getAppVersionCode(context, context.getPackageName());
    }

    /**
     * 根据packageName包名的应用获取应用版本名称,如未安装返回null
     * 
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppVersion(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 根据packageName包名的应用获取应用版本名称,如未安装返回null
     *
     * @param context
     * @param packageName
     * @return
     */
    public static int getAppVersionCode(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return 0;
        }
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    /**
     *根据packageName包名的应用获取应用信息,如未安装返回null
     * @param context
     * @param packageName
     * @return
     */
    public static PackageInfo getAppInfo(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            return null;
        }
    }
    
    /**
     * 判断打开新闻，按返回键是否要回到列表页面
     * 1、网易新闻内部点击链接：不回到列表页
     * 2、外部浏览器点击链接：不回到列表
     */
    public static boolean shouldStartMain(Context context) {
    	try {
    		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        	List<RunningTaskInfo> tasksInfoList = am.getRunningTasks(1);
        	
        	if (tasksInfoList == null || tasksInfoList.size() == 0) {
        		return false;
        	}
        	
        	String appName = context.getPackageName();
        	RunningTaskInfo taskInfo = tasksInfoList.get(0);
        	if (taskInfo.numActivities == 1 || !appName.equals(taskInfo.baseActivity.getPackageName())) {
    			return true;
        	}
		} catch (Exception e) {
		}
    	return false;
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
    

	// 得到图片的缓存的位置
	public static final String CACHE_IMG_DIR = "images";


	public static File getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir;
	}

	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasGingerbreadMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean hasJellyBeanMR2() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
	}

	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	public static boolean IfOnMainThread() {
		return Looper.myLooper() != Looper.getMainLooper();
	}
	/**
	 * Get the external app cache directory.
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	private static File getExternalCacheDir(Context context) {
		// TODO: This needs to be moved to a background thread to ensure no disk
		// access on the
		// main/UI thread as unfortunately getExternalCacheDir() calls mkdirs()
		// for us (even
		// though the Volley library will later try and call mkdirs() as well
		// from a background
		// thread).
		return context.getExternalCacheDir();
	}
	public static final Charset US_ASCII = Charset.forName("US-ASCII");
	public static final Charset UTF_8 = Charset.forName("UTF-8");

	public static String readFully(Reader reader) throws IOException {
		try {
			StringWriter writer = new StringWriter();
			char[] buffer = new char[1024];
			int count;
			while ((count = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, count);
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}

	/**
	 * Deletes the contents of {@code dir}. Throws an IOException if any file
	 * could not be deleted, or if {@code dir} is not a readable directory.
	 */
	public static void deleteContents(File dir) throws IOException {
		File[] files = dir.listFiles();
		if (files == null) {
			throw new IOException("not a readable directory: " + dir);
		}
		for (File file : files) {
			if (file.isDirectory()) {
				deleteContents(file);
			}
			if (!file.delete()) {
				throw new IOException("failed to delete file: " + file);
			}
		}
	}

	public static void closeQuietly(/* Auto */Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (RuntimeException rethrown) {
				throw rethrown;
			} catch (Exception ignored) {
			}
		}
	}

	/** 检查是否有网络 */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	/** 检查是否是WIFI */
	public static boolean isWifi(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI)
				return true;
		}
		return false;
	}

	/** 检查是否是移动网络 */
	public static boolean isMobile(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE)
				return true;
		}
		return false;
	}

	private static NetworkInfo getNetworkInfo(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	public static boolean isSpecialType(String url) {
		boolean isSpecial = url.startsWith("file:") || url.startsWith("video:") || url.startsWith("android.resource:");
		return isSpecial;
	}

	public final static int dipToPx(Context context, int dipValue) {
		return (int) (dipValue * context.getResources().getDisplayMetrics().density + 0.5f);
	}

	public final static int convertPixelToDip(int pixel, Resources res) {
		return (int) (((float) pixel - 0.5f) / res.getDisplayMetrics().density);
	}

	public static float getScreenDpi(Resources res) {
		return res.getDisplayMetrics().densityDpi / 160;
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
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			// Print exception
			e.printStackTrace();
			// Default to return 1 core
			return 1;
		}
	}

	/**
	 * 倒计时的时间格式
	 * 
	 * @param m
	 * @return
	 */
	public static String getFormatTimeStr(long m) {
		if (m > 0) {
			m = m / 1000;
			if (m > 0) {
				int nDay = (int) m / (24 * 60 * 60);
				int nHour = (int) (m - nDay * 24 * 60 * 60) / (60 * 60);
				int nMinute = (int) (m - nDay * 24 * 60 * 60 - nHour * 60 * 60) / 60;
				int nSecond = (int) m - nDay * 24 * 60 * 60 - nHour * 60 * 60 - nMinute * 60;
				StringBuilder sb = new StringBuilder();
				if (nHour > 0) {
					if (nHour < 10) {
						sb.append("0");
					}
					sb.append(nHour + ":");
				} else {
					sb.append("00:");
				}
				if (nMinute > 0 || nHour > 0) {
					if (nMinute < 10) {
						sb.append("0");
					}
					sb.append(nMinute + ":");
				} else {
					sb.append("00:");
				}
				if (nHour > 0 || nMinute > 0 || nSecond > 0) {
					if (nSecond < 10) {
						sb.append("0");
					}
					sb.append(nSecond);
				} else {
					sb.append("00");
				}

				return sb.toString();
			}
		}

		return "";
	}

	/**
	 * 倒计时的时间格式
	 * 
	 * @param m
	 * @return
	 */
	public static String getSecondFormatTimeStr(long m) {
		if (m > 0) {
			m = m / 1000;
			if (m > 0) {
				int nDay = (int) m / (24 * 60 * 60);
				int nHour = (int) (m - nDay * 24 * 60 * 60) / (60 * 60);
				int nMinute = (int) (m - nDay * 24 * 60 * 60 - nHour * 60 * 60) / 60;
				int nSecond = (int) m - nDay * 24 * 60 * 60 - nHour * 60 * 60 - nMinute * 60;
				StringBuilder sb = new StringBuilder();
				if (nHour > 0 || nMinute > 0 || nSecond > 0) {
					if (nSecond < 10) {
						sb.append("0");
					}
					sb.append(nSecond);
				} else {
					sb.append("00");
				}

				return sb.toString();
			}
		}
		return "";
	}

	public static String getLocalIpAddress(Context context) {
		/**
		 * Get IP address from first non-localhost interface
		 * 
		 * @param ipv4
		 *            true=return ipv4, false=return ipv6
		 * @return address or empty string
		 */
		boolean useIPv4 = true;
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port
																// suffix
								return delim < 0 ? sAddr : sAddr.substring(0, delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "";

	}

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}

    /**
     * is support front camera
     * @param context
     * @return
     */
    public static boolean isSupportFrontCamare(Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    /*
 * Generate a new OpenUDID
 */
    public static  String generateOpenUDID(Context context) {
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


    public static Handler getMainThreadHandler(Context context){
        return new Handler(Looper.getMainLooper());
    }

    /**
     * 判断是否支持低功耗蓝牙
     *
     * @return
     */
    public static boolean isBlueSupport() {
        return getSDKVersionNum() >= MINI_BLUETOOTH_SDK_SUPPORT;
    }

    private static int getSDKVersionNum() {
        return Integer.parseInt(Build.VERSION.SDK);
    }

    public static void throwArgumentExeception(String argumentStr) {
        throw new IllegalArgumentException(argumentStr);
    }

    /**
     * 获得本地的mac地址
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * check whether current application running at forground or background
     *
     * @param context
     * @return true forground
     */
    public static boolean isProcessInFG(Context context) {
        boolean bForeground = false;
        if (context == null) {
            return bForeground;
        }
        ActivityManager activityManager =(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return bForeground;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return bForeground;
        }

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (context.getPackageName() != null
                    && !context.getPackageName().equals("")
                    && appProcess.processName.contains(context.getPackageName())
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                bForeground = true;
                break;
            }
        }
        return bForeground;
    }
}
