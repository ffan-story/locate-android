package com.libs.utils;

import java.util.List;
import java.util.Map;

/**
 * Created by mengmeng on 16/5/27.
 * 处理一些collection的通用类，主要目的就是防止在使用collection的时候出现outofindex的崩溃
 */
public class CollectionUtils {

  public static Object getList(List<Object> list, int pos) {
    if (list == null || list.size() <= pos) {
      return null;
    }
    return list.get(pos);
  }

  public static Object getMap(Map<Object, Object> map, Object key) {
    if (map == null || map.isEmpty() || !map.containsKey(key)) {
      return null;
    }
    return map.get(key);
  }

  public static Object getArray(Object[] arrays, int pos) {
    if (arrays == null || arrays.length <= pos) {
      return null;
    }
    return arrays[pos];
  }
}
