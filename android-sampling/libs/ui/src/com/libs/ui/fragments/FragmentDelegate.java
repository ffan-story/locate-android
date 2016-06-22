package com.libs.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Window;

import com.libs.ui.activities.BaseActivity;
import com.wanda.logger.log.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meng
 * @Title: FragmentDelegate.java
 * @Description: 注意是使用的时�?在平板一个activity中加载多个fragment的时候，�?��将参数加到一个list中；如果是一个acitivity只有�?��fragment此时可以将相关的bundle传进�?
 * @date 2014-7-18 下午3:43:05
 */
public class FragmentDelegate {
    public static String FRAGMENTT_NAME = "fragment_name";
    public static String FRAGMENTT_ARGU_LIST = "fragment_argu_list";
    public static String FRAGMENTT_TAG = "fragment_tag";
    public static String FRAGMENT_CONTAIN_ID = "fragment_contain_id";
    public static String ACTIVITY_CONTAIN_ID = "activity_contain_id";
    public static String EXTRA_PARAMS = "extra_param";
    public static String ACTIVITY_WINDOWS_FEATURE = "activity_windows_feature";
    public static String ACTIVITY_WINDOWS_FLAG = "activity_windows_flag";
    private  BaseActivity mFActivity;
    private Bundle mParams;
    private ArrayList<  FragmentParams> mList;
    public List<String> mTagList = new ArrayList<String>();

    public FragmentDelegate(BaseActivity activity, Bundle param) {
        if (param == null) {
            throwException("param");
        }
        if (!(activity instanceof FragmentActivity)) {
            throw new IllegalArgumentException("The activiy should be FragmentActivity");
        }
        mFActivity = ( BaseActivity) activity;
        mParams = param;
        requestWindowsAttr();
    }

    public FragmentDelegate( BaseActivity activity, ArrayList<  FragmentParams> list, Bundle param) {
        if (param == null || list == null || list.isEmpty()) {
            throwException("argument");
        }
        if (!(activity instanceof FragmentActivity)) {
            throw new IllegalArgumentException("The activiy should be FragmentActivity");
        }
        mFActivity = ( BaseActivity) activity;
        mList = list;
        mParams = param;
        requestWindowsAttr();
    }

    public void setFragmentParamList(ArrayList<  FragmentParams> list) {
        mList = list;
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: requestWindowsAttr
     * @Description: TODO
     */
    public void requestWindowsAttr() {
        requestActivityWindowFeature();
        requestActivityWindowsFlag();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: requestActivityWindowFeature
     * @Description: TODO
     */
    public void requestActivityWindowFeature() {
        int windowsFrature = mParams.getInt(ACTIVITY_WINDOWS_FEATURE);
        if (windowsFrature == 0) {
            windowsFrature = Window.FEATURE_NO_TITLE;
        }
        mFActivity.requestWindowFeature(windowsFrature);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: requestActivityWindowsFlag
     * @Description: TODO
     */
    public void requestActivityWindowsFlag() {
        int[] flags = mParams.getIntArray(ACTIVITY_WINDOWS_FLAG);
        if (flags != null) {
            Window window = mFActivity.getWindow();
            for (int flag : flags) {
                window.setFlags(flag, flag);
            }
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: requestFragment
     * @Description: TODO
     */
    public List<String> requestFragment() {
        int activity_id = mParams.getInt(ACTIVITY_CONTAIN_ID);
        if (activity_id > 0) {
            mFActivity.setContentView(activity_id);
        }
        List<String> tagList = new ArrayList<String>();
        if (mList != null && mList.size() > 0) {
            for (  FragmentParams param : mList) {
                String tag = param.getmFragmentTag();
                int fragment_container = param.getmFragmentId();
                String fragmentname = param.getmFragmentName();
                Bundle argument = param.getmBundle();
                tagList.add(tag);
                addFragmentByTag(tag, fragment_container, fragmentname, argument);
            }
        } else {
            String tag = mParams.getString(FRAGMENTT_TAG);
            int fragment_container = mParams.getInt(FRAGMENT_CONTAIN_ID);
            String fragmentname = mParams.getString(FRAGMENTT_NAME);
            Bundle argument = mParams.getBundle(EXTRA_PARAMS);
            if (fragment_container == 0 || fragment_container == -1) {
                fragment_container = android.R.id.content;
            }
            tagList.add(tag);
            addFragmentByTag(tag, fragment_container, fragmentname, argument);
        }
        return tagList;
    }

    public Fragment addFragment(String tag, String fragmentname, Bundle argument) {
        int container = android.R.id.content;
        return addFragment(tag, container, fragmentname, argument);
    }

    /**
     * @param @param  tag
     * @param @param  container
     * @param @param  fragmentname
     * @param @return
     * @return Fragment
     * @throws
     * @Title: addFragmentByTag
     * @Description: TODO
     */
    public Fragment addFragmentByTag(String tag, int container, String fragmentname, Bundle argument) {
        if (mParams == null) {
            throwException("argument");
        }
        FragmentManager fm = mFActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment != null) {
            if(fragment.isAdded()){
                Logger.d(tag + "  fragment is added and fragment is added");
                ft.show(fragment);
                return fragment;
            }else {
                Logger.d(tag + "  fragment is detached and fragment is added  again");
                ft.attach(fragment);
                ft.commitAllowingStateLoss();
                return fragment;
            }
        }
        return addFragment(tag, container, fragmentname, argument);
    }

    public Fragment getFRagmentByTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            FragmentManager fm = mFActivity.getSupportFragmentManager();
            List<Fragment> list = fm.getFragments();
            return fm.findFragmentByTag(tag);
        }
        return null;
    }

    /**
     * @param @param  tag
     * @param @param  container
     * @param @param  fragname
     * @param @param  argument
     * @param @return
     * @return Fragment
     * @throws
     * @Title: addFragment
     * @Description: TODO
     */
    public Fragment addFragment(String tag, int container, String fragname, Bundle argument) {
        FragmentManager fm = mFActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = Fragment.instantiate(mFActivity, fragname, argument);
        if (container == 0) {
            ft.add(fragment, tag);
        } else {
            ft.add(container, fragment, tag);
        }
        mTagList.add(tag);
        Logger.d(tag + "  fragment is new added");
        ft.addToBackStack(tag);
        ft.commitAllowingStateLoss();

        return fragment;
    }

    /**
     *
     * @param tag
     * @param container
     * @param fragname
     * @param argument
     * @return
     */
    public Fragment replaceFragment(String tag, int container, String fragname, Bundle argument) {
        FragmentManager fm = mFActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment != null) {
            if(fragment.isAdded()){
                Logger.d(tag + "  fragment is added and fragment is added");
                ft.show(fragment);
                return fragment;
            }else {
                Logger.d(tag + "  fragment is detached and fragment is added  again");
                ft.attach(fragment);
                ft.commitAllowingStateLoss();
                return fragment;
            }
        }
        fragment = Fragment.instantiate(mFActivity, fragname, argument);
        if (container == 0) {
            ft.add(fragment, tag);
        } else {
            ft.replace(container, fragment, tag);
        }
        if(!mTagList.contains(tag)){
            mTagList.add(tag);
        }
        Logger.d(tag + "  fragment is new added");
        ft.addToBackStack(tag);
        ft.commitAllowingStateLoss();

        return fragment;
    }
    /**
     * 如果当前的activity的backstack栈中含有fragment，就将其推出，然后返回true。否则返回false
     * @return
     */
    public boolean popupFragment(){
        FragmentManager fm = mFActivity.getSupportFragmentManager();
        Logger.e("fm.getBackStackEntryCount(): " + fm.getBackStackEntryCount());
        if(fm.getBackStackEntryCount() >1){
            fm.popBackStackImmediate();
            return true;
        }
        return false;
    }

    /**
     * @param @param name
     * @return void
     * @throws
     * @Title: throwException
     * @Description: TODO
     */
    private void throwException(String name) {
        throw new IllegalArgumentException("The params " + name + " cannnot be null");
    }

    /**
     * @param @param  context
     * @param @param  contain_id
     * @param @param  fragment_id
     * @param @param  fragment_tag
     * @param @param  fragmentname
     * @param @param  classname
     * @param @param  extraBundle
     * @param @return
     * @return Intent
     * @throws
     * @Title: getIntent
     * @Description: TODO
     */
    public static Intent getIntent(Context context, int contain_id, int fragment_id, String fragment_tag, String fragmentname, Class classname, Bundle extraBundle) {
        Intent intent = new Intent(context, classname);
        Bundle bundle = new Bundle();
        bundle.putInt(ACTIVITY_CONTAIN_ID, contain_id);
        bundle.putInt(FRAGMENT_CONTAIN_ID, fragment_id);
        bundle.putString(FRAGMENTT_NAME, fragmentname);
        bundle.putString(FRAGMENTT_TAG, fragment_tag);
        if (extraBundle != null) {
            bundle.putBundle(EXTRA_PARAMS, extraBundle);
        }
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * @param @param  context
     * @param @param  list
     * @param @param  classname
     * @param @return
     * @return Intent
     * @throws
     * @Title: getIntent
     * @Description: TODO
     */
    public static Intent getIntent(Context context, ArrayList<  FragmentParams> list, int contain_id, Class classname) {
        if (list == null && list.isEmpty()) {
            return null;
        }
        Intent intent = new Intent(context, classname);
        Bundle bundle = new Bundle();
        bundle.putInt(ACTIVITY_CONTAIN_ID, contain_id);
        intent.putParcelableArrayListExtra(FRAGMENTT_ARGU_LIST, list);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * send event from activity to fragment
     *
     * @param obj
     */
    public void postFragmentEvent(Object obj) {
        if (mTagList != null) {
            List<String> list = mTagList;
            if (list != null && list.isEmpty()) {
                for (String tag : list) {
                    if (!TextUtils.isEmpty(tag)) {
                        Fragment fragment = mFActivity.getSupportFragmentManager().findFragmentByTag(tag);
                        if (fragment instanceof   RootFragment) {
                            ((  RootFragment) fragment).onActivityEventPost(obj);
                        }
                    }
                }
            }
        }
    }
}
