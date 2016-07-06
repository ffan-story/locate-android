package com.feifan.sampling.scan;

import android.text.TextUtils;

import com.feifan.sampling.base.log.config.APPLogConfig;
import com.feifan.sampling.scan.model.PatchSpotModel;
import com.libs.utils.DateTimeUtils;
import com.libs.utils.ExceptionUtils;
import com.libs.utils.SystemUtils;
import com.mm.beacon.IBeacon;
import com.wanda.logger.file.CVSModel;
import com.wanda.logger.file.CvsFileRequest;
import com.wanda.logger.log.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengmeng on 16/6/16.
 */
public class ScanHelper {
    public static void SaveIBeacon(String[] header, List<String[]> list, String name){
        CVSModel model = new CVSModel();
        model.setHeader(header);
        model.setList(list);
        CvsFileRequest request = new CvsFileRequest(new APPLogConfig(name));
        request.setLog(model);
        Logger.writeRequest(request);
    }

    public static List<PatchSpotModel> buildPatchUploadList(List<IBeacon> list, String spotid){
        List<PatchSpotModel> patchList = new ArrayList<PatchSpotModel>();
        if(list != null && !list.isEmpty()){
            for (int i = 0; i < list.size(); i++) {
                IBeacon beacon = list.get(i);
                if(beacon != null){
                    PatchSpotModel model = new PatchSpotModel();
                    model.setDevice(SystemUtils.getBrand());
                    model.setMajor(String.valueOf(beacon.getMajor()));
                    model.setMinor(String.valueOf(beacon.getMinor()));
                    model.setSpot_id(spotid);
                    model.setUuid(beacon.getProximityUuid());
                    model.setRssi(String.valueOf(beacon.getRssi()));
                    model.setTime(String.valueOf(beacon.getTime()));
                    patchList.add(model);
                }
            }
        }
        return patchList;
    }

    public static String getFilePathStr(String path,float x,float y,int interval,int count,String direction){
        if (!TextUtils.isEmpty(path)){
            String beaconName =
                    path + "_" + DateTimeUtils.getCurrentTime("yyyy-MM-dd HH-mm-ss") + "_"
                            + interval + "_" + count+ "_" +direction+"_"+x+"_"+y;
            return beaconName;
        }else {
            ExceptionUtils.throwArgumentExeception("the path can not be null");
        }
        return "";
    }
}
