package com.libs.base.model;


public class BaseJsonBean<T> extends BaseBean {
	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
