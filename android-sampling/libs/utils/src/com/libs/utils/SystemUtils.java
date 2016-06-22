package com.libs.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class SystemUtils {

    //低功耗蓝牙最小支持api 18
    public static int MINI_BLUETOOTH_SDK_SUPPORT = 18;
    
    public static String getBrand(){
    	return Build.BRAND;
    }
    
    public static boolean isMIUI(){
    	String miui = "Xiaomi";
        return miui.equals(getBrand());
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
     * 4.0+获取虚拟导航高度
     * 
     * @param context
     * @return
     */
    public int getNavigationBarHeight(Context context) {
    	Resources resources = context.getResources();
    	int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    	if (resourceId > 0) {
    	    return resources.getDimensionPixelSize(resourceId);
    	}
    	return 0;
    }
    
    /**
     * 获取手机状态栏高度
     * 
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
    	Rect rect = new Rect();
		((FragmentActivity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		return rect.top;
    }
    
    /**
     * 获取应用窗口高度
     * 
     * @param context
     * @return
     */
    public static int getAppWindowHeight(Context context) {
    	Rect rect = new Rect();
    	((FragmentActivity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
    	return rect.bottom - rect.top;
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

    private static String VERSTION_FIRST_START_APP_KEY = "first_start_%s_app_key";
    private static String VERSTION_PREF = "VERSTION_PREF";
    
   
    public static String FIRST_INIT_DATA_KEY = "first_init_data_key";
    

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
     * 判断当前的最前的栈顶是不是当前的app
     * @param context
     * @return
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

	public static boolean IfOnMainThread() {
		return Looper.myLooper() != Looper.getMainLooper();
	}

	/**
	 * Check how much usable space is available at a given path.
	 * 
	 * @param path
	 *            The path to check
	 * @return The space available in bytes
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(9)
	public static long getUsableSpace(File path) {
		if (PlatformUtils.hasGingerbread()) {
			return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	/*
	 * private File getDiskCacheDir(Context context, String uniqueName) { final
	 * String cachePath = context.getCacheDir().getPath(); return new
	 * File(cachePath + File.separator + uniqueName); }
	 */

	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir

		// TODO: getCacheDir() should be moved to a background thread as it
		// attempts to create the
		// directory if it does not exist (no disk access should happen on the
		// main/UI thread).
		final String cachePath;
		if (isExternalMounted() && null != getExternalCacheDir(context)) {
			cachePath = getExternalCacheDir(context).getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}

		Log.i("Cache dir", cachePath + File.separator + uniqueName);
		return new File(cachePath + File.separator + uniqueName);
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

	@SuppressLint("NewApi")
	private static boolean isExternalMounted() {
		if (PlatformUtils.hasGingerbread()) {
			return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable();
		}
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
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

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
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

    /**
     * 判断是否支持对焦模式,api8 以下的都是不支持对焦
     * @return
     */
    public boolean isSupportFocusMode(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            return true;
        }
        return false;
    }

    /**
     * check obj is null or empty
     * @param obj
     * @return
     */
    public static boolean isNull(Object obj){
        if(obj != null){
            if (obj.getClass().isArray()){
               return ((Object[])obj).length > 0 ? false:true;
            }else if(obj instanceof String){
                return TextUtils.isEmpty((String)obj);
            }else if (obj instanceof Collection<?>){
                return ((Collection<?>)obj).isEmpty();
            }else if (obj instanceof Integer){
                return false;
            }else if (obj instanceof Float){
                return false;
            }else if (obj instanceof Double){
                return false;
            }else if(obj instanceof Map<?,?>){
                return ((Map<?,?>)obj).isEmpty();
            }
        }
        return true;
    }
}
