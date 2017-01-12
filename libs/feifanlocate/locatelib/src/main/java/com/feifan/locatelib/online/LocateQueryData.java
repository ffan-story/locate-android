package com.feifan.locatelib.online;

import com.feifan.baselib.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 定位查询数据
 * Created by xuchunlei on 16/9/14.
 */
public class LocateQueryData {
    /**
     * 使用算法
     */
    public String algorithm;
    /**
     * 位置
     */
    public String position;
    /**
     * 数据
     */
    public Map<String, Float> tensor = new HashMap<>();

    /**
     * 更新数据
     * @param tensor
     */
    public void upDateTensor(Map<String, Float> tensor) {
        this.tensor.clear();
        this.tensor.putAll(tensor);
    }

}
