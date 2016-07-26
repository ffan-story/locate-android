package com.libs.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * the url util class 用于处理url的一些公用的方法
 * 
 * @author meng
 * 
 */
public class UrlUtil {

	/**
	 * get url by host action params
	 * 
	 * @param host
	 * @param action
	 * @param map
	 * @return
	 * 
	 *         String
	 */
	public static String getUrlString(String host, String action, Map<String, Object> map) {
		if (map != null && !TextUtils.isEmpty(host) && !TextUtils.isEmpty(action)) {
			if (host.endsWith("/") || action.endsWith("/")) {
				throw new IllegalStateException("The host or action should be end with /");
			}
			String mapStr = mapToString(map);
			return host + mapStr;
		} else {
			throw new IllegalStateException("The host url or params map cannot be null");
		}
	}

	/**
	 * 
	 * @param params
	 * @return UrlUtil
	 * 
	 */
	public static String mapToParamsString(Map<String, Object> params) {
		if (params != null) {
			StringBuilder paramsStr = new StringBuilder();
			if (params == null) {
				return paramsStr.toString();
			}
			for (Map.Entry<String, Object> e : params.entrySet()) {
				if (e != null && !TextUtils.isEmpty(e.getKey())) { 
					try {
						paramsStr.append("&").append(e.getKey()).append("=").append(URLEncoder.encode((e.getValue() == null ? "" : e.getValue().toString()), "UTF-8"));
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
				}
			}
			if (paramsStr != null && paramsStr.length() > 1) {
				return paramsStr.toString().substring(1);
			}
		}
		return "";
	}
	/**
	 * 
	 * @param params
	 * @return UrlUtil
	 * 
	 */
	public static String mapToString(Map<String, Object> params) {
		if (params != null) {
			StringBuilder paramsStr = new StringBuilder();
			if (params == null) {
				return paramsStr.toString();
			}
			for (Map.Entry<String, Object> e : params.entrySet()) {
				if (e != null && !TextUtils.isEmpty(e.getKey()) &&e.getValue() != null && !TextUtils.isEmpty((CharSequence) e.getValue())) { 
					try {
						paramsStr.append("&").append(e.getKey()).append("=").append(URLEncoder.encode((e.getValue() == null ? "" : e.getValue().toString()), "UTF-8"));
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
				}
			}
			if (paramsStr != null && paramsStr.length() > 1) {
				return paramsStr.toString().substring(1);
			}
		}
		return "";
	}

	/**
	 * 
	 * @param params
	 * @return UrlUtil
	 * 
	 */
	public static String mapToSignString(Map<String, Object> params) {
		if (params != null) {
			StringBuilder paramsStr = new StringBuilder();
			if (params == null) {
				return paramsStr.toString();
			}
			for (Map.Entry<String, Object> e : params.entrySet()) {
				if (e != null && !TextUtils.isEmpty(e.getKey()) && e.getValue() != null && !TextUtils.isEmpty((CharSequence) e.getValue())) {
					paramsStr.append("&").append(e.getKey()).append("=").append(e.getValue() == null ? "" : e.getValue().toString());
				}
			}
			if (paramsStr != null && paramsStr.length() > 1) {
				return paramsStr.toString().substring(1);
			}
		}
		return "";
	}

	public String Utf8URLencode(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c >= 0 && c <= 255) {
				result.append(c);
			} else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}
}
