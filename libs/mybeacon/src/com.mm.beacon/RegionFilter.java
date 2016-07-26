package com.mm.beacon;


import com.mm.beacon.data.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengmeng on 15/8/20.
 */
public class RegionFilter {
    private List<Region> mRegionFilter = new ArrayList<Region>();
    private int mMatchNum = 3;
    private final int REGION_MATCHED_NUM = 3;
    public List<Region> getRegionList() {
        return mRegionFilter;
    }

    public void setRegionList(List<Region> mRegionFilter) {
        this.mRegionFilter = mRegionFilter;
    }

    public boolean isEmpty(){
        return  mRegionFilter.isEmpty();
    }


    public int getmMatchNum() {
        return mMatchNum;
    }

    public void setmMatchNum(int mMatchNum) {
        this.mMatchNum = mMatchNum;
    }

    public void plusRegion(){
        if(mMatchNum < REGION_MATCHED_NUM){
            mMatchNum ++;
        }
    }

    public void subRegion(){
        if(mMatchNum > 0){
            mMatchNum --;
        }
    }

    public boolean isInside(){
        return  mMatchNum > 0 ? true:false;
    }
}
