package com.mm.beacon;

import android.text.TextUtils;

import com.mm.beacon.data.FilterBeacon;
import com.mm.beacon.data.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengmeng on 15/8/20.
 */
public class BeaconFilter {
    private List<String> mUidList = new ArrayList<String>();
    private  List<Region> mScanList = new ArrayList<Region>();

    public List<FilterBeacon> getBeaconList() {
        return mBeaconList;
    }

    public void setBeaconList(List<FilterBeacon> mBeaconList) {
        this.mBeaconList = mBeaconList;
    }

    private List<FilterBeacon> mBeaconList = new ArrayList<FilterBeacon>();
    public BeaconFilter(){
//        initFilter();
    }


    private void initFilter(){
        mUidList.add("a3fce438-627c-42b7-ab72-dc6e55e137ac");
        mUidList.add("01122334-4556-6778-899a-abbccddeeff0");
        mUidList.add("fda50693-a4e2-4fb1-afcf-c6eb07647825");
        mUidList.add("A4C58760-6965-47D5-8B0A-A4C1BCD9069D");
        mUidList.add("289B312F-93BC-4071-9BA8-3A607D873B61");
        mUidList.add("E9375300-5499-455C-B3F4-ADC248F1B8E8");
        mUidList.add("DE1EBC82-AF18-4F8E-9B25-3FDC20400020");
        mUidList.add("CC33B450-D7CA-43C9-A100-0041C60CD2F5");
        mUidList.add("05C39AC0-7745-4E74-9A1C-850047D1E99B");
        mUidList.add("0E07016E-04B8-4C83-96E6-C848EFC8A010");
        mUidList.add("65E198C0-9CFB-49F6-8896-3647CFEFEECD");
        mUidList.add("F800F0BE-D61F-4BE6-8F0C-5785755DDB51");
        mUidList.add("3B060483-46EA-4524-8304-B051F91C7FAC");
        mUidList.add("ECB33B47-781F-4C16-8513-73FCBB7134F2");
        mUidList.add("5E8F8772-362A-485B-B0B5-DCEC6E5634AA");
        mUidList.add("5926877A-0B2D-4DE9-A560-C782D9407343");
        mUidList.add("DEDF81EF-F2FF-4931-A1D6-D64215DA2517");
        mUidList.add("78C298EB-205A-4EE3-9D85-054FD99D83E3");
        for (int i = 0; i < mUidList.size(); i++) {
            String uid = mUidList.get(i);
            if(!TextUtils.isEmpty(uid)){
                mScanList.add(new Region(uid, uid, null, null));
            }
        }
    }

    public List<Region> getFilterList(){
        return mScanList;
    }


    public void setFilterList(List<String> uidList){
        if(uidList != null && !uidList.isEmpty()){
            mUidList.clear();
            mUidList.addAll(uidList);
            initFilter();
        }
    }

    public boolean isEmpty(){
        return mBeaconList == null ? true : mBeaconList.isEmpty();
    }


}
