package com.libs.ui.fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @Title: FragmentParams.java
 * @Description: TODO
 * @author meng
 * @date 2014-7-18 下午2:29:09
 * @version
 */
public class FragmentParams implements Parcelable {
	public int mContainId;
	public int mFragmentId;
	public String mFragmentTag;
	public String mFragmentName;
	public Bundle mBundle;
	public List<Bundle> mlist = new ArrayList<Bundle>();

	public FragmentParams(int contain_id, int fragment_id, String fragment_tag, String fragment_name, Bundle bundle) {
		mContainId = contain_id;
		mFragmentId = fragment_id;
		mFragmentTag = fragment_tag;
		mFragmentName = fragment_name;
		mBundle = bundle;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mContainId);
		dest.writeInt(mFragmentId);
		dest.writeString(mFragmentName);
		dest.writeString(mFragmentTag);
		dest.writeBundle(mBundle);
	}

	public static final Creator<FragmentParams> CREATOR = new Creator<FragmentParams>() {
		public FragmentParams createFromParcel(Parcel in) {
			return new FragmentParams(in);
		}

		public FragmentParams[] newArray(int size) {
			return new FragmentParams[size];
		}
	};

	private FragmentParams(Parcel in) {
		mContainId = in.readInt();
		mFragmentId = in.readInt();
		mFragmentName = in.readString();
		mFragmentTag = in.readString();
		mBundle = in.readBundle();
	}

	public int getmContainId() {
		return mContainId;
	}

	public int getmFragmentId() {
		return mFragmentId;
	}

	public String getmFragmentTag() {
		return mFragmentTag;
	}

	public String getmFragmentName() {
		return mFragmentName;
	}

	public Bundle getmBundle() {
		return mBundle;
	}
}
