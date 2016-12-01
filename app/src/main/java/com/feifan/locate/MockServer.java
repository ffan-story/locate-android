package com.feifan.locate;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.feifan.baselib.utils.AssetUtils;
import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.provider.LocateData;
import com.feifan.locate.provider.LocateData.Building;
import com.feifan.locate.provider.LocateData.Zone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created by xuchunlei on 16/9/19.
 */
public class MockServer {

    private MockServer(){

    }

    public static void requestBuildingData(Context context) {
        final ContentResolver resolver = context.getContentResolver();
        final ContentValues values = new ContentValues();
        AssetUtils.readLines(context, "plaza.data", new AssetUtils.Transactor() {
            @Override
            public void transact(String line) {
                values.clear();
                String[] lineValues = line.split(",");
                values.put(Building._ID, Integer.valueOf(lineValues[0]));
                values.put(Building.NAME, lineValues[1]);
                values.put(Building.BUILDING_ID, lineValues[2]);
                values.put(Building.MIN_FLOOR, Integer.valueOf(lineValues[3]));
                resolver.insert(LocateData.Building.CONTENT_URI, values);
            }
        });
    }

    public static void requestZoneData(Context context) {
        final ContentResolver resolver = context.getContentResolver();
        final ContentValues values = new ContentValues();
        AssetUtils.readLines(context, "zone.data", new AssetUtils.Transactor() {
            @Override
            public void transact(String line) {
                values.clear();
                String[] lineValues = line.split(",");
                values.put(Zone._ID, Integer.valueOf(lineValues[0]));
                values.put(Zone.NAME, lineValues[1]);
                values.put(Zone.FLOOR_NO, Integer.valueOf(lineValues[2]));
                values.put(Zone.TITLE, lineValues[3]);
                values.put(Zone.BUILDING, Integer.valueOf(lineValues[4]));
                resolver.insert(Zone.CONTENT_URI, values);
            }
        });

    }

    public static Map<String, Float> TENSOR_DATA_A22 = new HashMap<>();
    public static Map<String, Float> TENSOR_DATA_860100010060300001 = new HashMap<>();
    static {
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42513", -91f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42514", -79f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42516", -91f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42559", -89.5f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42567", -78f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42614", -90f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42651", -85.5f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42793", -86f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42857", -69.5f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42999", -90f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43015", -85f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43096", -75f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43124", -84f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43131", -88f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43152", -73f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43157", -76f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43202", -90f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43228", -89f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43255", -89f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43296", -79.5f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43303", -82f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43328", -90f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43443", -91f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43482", -83.5f);
        TENSOR_DATA_A22.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43515", -86f);

        // 石景山万达测试数据

        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42492", -90f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42496", -82f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42532", -85f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42533", -86f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42538", -78f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42569", -79f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42571", -82f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42576", -79f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42578", -76f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42590", -90f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42643", -85f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42646", -83f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42647", -84f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42656", -91f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42794", -91f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42831", -80f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42837", -81f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42859", -81f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42871", -87f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42880", -87f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43018", -79f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43075", -75f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43078", -78f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43153", -87f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43179", -88f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43194", -88f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43229", -78f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43254", -82f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43286", -81f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43292", -82f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43352", -80f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43358", -76f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43388", -83f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43417", -85f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43425", -80f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43447", -89f);
        TENSOR_DATA_860100010060300001.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43517", -80f);

    }

    public static void createImapForRtMap(Context context) {

        File rtmapDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath().
                concat(File.separator).concat("rtmapwh").concat(File.separator).concat("mdata"));
        if(!rtmapDir.exists()) {
            rtmapDir.mkdirs();
        }

        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) {
            for (String filename : files) {
                if(filename.length() == 32) {
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = assetManager.open(filename);
                        File outFile = new File(rtmapDir, filename);
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                    } catch(IOException e) {
                        Log.e("tag", "Failed to copy asset file: " + filename, e);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                // NOOP
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                // NOOP
                            }
                        }
                    }
                }
            }
        }
    }
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private static float getScale(int id) {
        switch (id) {
            case 2:
            case 3:
            case 4:
                if(Build.MANUFACTURER.contains("Meizu")) {
                    return 0.136f * 1.333f;
                }
                return 0.136f;
            case 5:
            case 6:
                if(Build.MANUFACTURER.contains("Meizu")) {
                    return 0.226f * 1.333f;
                }
                return 0.226f;
            default:
                return 1f;
        }

    }
}
