package com.feifan.sampling.scan;

import com.feifan.sampling.base.log.config.APPLogConfig;
import com.feifan.sampling.scan.model.PatchSpotModel;
import com.libs.utils.SystemUtils;
import com.mm.beacon.data.IBeacon;
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

    public static List<PatchSpotModel> buildPatchUploadList(List<IBeacon> list,String spotid){
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
}
