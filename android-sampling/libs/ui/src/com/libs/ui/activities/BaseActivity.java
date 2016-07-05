package com.libs.ui.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.libs.ui.R;
import com.libs.ui.activities.model.ActivityResultModel;
import com.libs.ui.fragments.FragmentDelegate;
import com.libs.ui.fragments.FragmentParams;
import com.libs.ui.fragments.RootFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BaseActivity extends AppCompatActivity {

    protected FragmentDelegate mFragmentDelegate;
    public List<String> mTagList = new ArrayList<String>();
    private List<OnBackPressListener> mBackPressedList = new ArrayList<OnBackPressListener>();
    private List<OnActivityResult> mActivityResultList = new ArrayList<OnActivityResult>();
    private Map<String ,Object> mDataMap = new HashMap<String,Object>();
    private final String FRAGMENT_DATA_KEY = "data";

    @Override
    protected void onCreate(Bundle arg0) {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            ArrayList<FragmentParams> list = intent.getParcelableArrayListExtra( FragmentDelegate.FRAGMENTT_ARGU_LIST);
            mFragmentDelegate = getFragmetnDelegate();
            int feature = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            if(bundle == null){
                bundle = new Bundle();
            }
            bundle.putInt(FragmentDelegate.ACTIVITY_WINDOWS_FEATURE,feature);
            if (list != null && !list.isEmpty()) {
                if (mFragmentDelegate == null) {
                    mFragmentDelegate = new FragmentDelegate(BaseActivity.this, list, bundle);
                }
                mFragmentDelegate.setFragmentParamList(list);
            } else {
                if (mFragmentDelegate == null) {
                    mFragmentDelegate = new FragmentDelegate(BaseActivity.this, bundle);
                }
            }
        }
//        StatusBarCompat.compat(this);
//        initWindow();
        super.onCreate(arg0);
//        StatusBarCompat.compat(this);
        mTagList = mFragmentDelegate.requestFragment();
        hidenInputMethord();
    }

    private void initWindow() {
        // 修改状态栏颜色，4.4+生效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus();
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorPrimaryDark));
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);//通知栏所需颜色
    }


    @TargetApi(19)
    protected void setTranslucentStatus() {
        Window window = getWindow();
        // Translucent status bar
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    private void hidenInputMethord(){
        if(getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void registerBackPressListener(OnBackPressListener backlistener){
        if(backlistener != null){
            mBackPressedList.add(backlistener);
        }
    }

    public void unRegisterBackPressListener(OnBackPressListener backlistener){
        if(backlistener != null){
            mBackPressedList.remove(backlistener);
        }
    }

    public void setFragmentData(Object obj){
        if(obj != null){
            mDataMap.put(FRAGMENT_DATA_KEY,obj);
        }
    }

    public Object getFragmentData(){
        return mDataMap.get(FRAGMENT_DATA_KEY);
    }


    public void registerActivityResultListener(OnActivityResult listener){
        if(listener != null){
            mActivityResultList.add(listener);
        }
    }

    public void unRegisteActivityResultListener(OnActivityResult listener){
        if(listener != null){
            mActivityResultList.remove(listener);
        }
    }


    public void notifyActivityResultListener( ActivityResultModel obj){
        if(obj != null) {
            for (int i = 0; i < mActivityResultList.size(); i++) {
                OnActivityResult backlistener = mActivityResultList.get(i);
                if (backlistener != null) {
                    backlistener.onActivityResult(obj);
                }
            }
        }
    }

    public void notifyBackPressListener(){
        for (int i=0;i<mBackPressedList.size();i++){
            OnBackPressListener backlistener = mBackPressedList.get(i);
            if(backlistener != null){
                backlistener.onBackPress();
            }
        }
    }
    /**
     * add fragment int this activity
     *
     * @param tag
     * @param fragmentname
     * @param argument
     */
    public void addFragment(String tag, int container, String fragmentname, Bundle argument) {
        if (mFragmentDelegate != null) {
            mFragmentDelegate.addFragment(tag, fragmentname, argument);
        }
    }

    protected FragmentDelegate getFragmetnDelegate() {
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(android.R.id.content);
        if (fragment instanceof RootFragment) {
            ((RootFragment) fragment).onBackPressed();
            return;
        }
        super.onBackPressed();
    }

    public Fragment getFragmentByTag(String tag) {
        if (mFragmentDelegate != null) {
            return mFragmentDelegate.getFRagmentByTag(tag);
        }
        return null;
    }

    public void addFragment(String tag, String fragmentname, Bundle bundle) {
        if (mFragmentDelegate != null) {
            mFragmentDelegate.addFragment(tag, fragmentname, bundle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:

                break;
            default:
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mBackPressedList != null && mBackPressedList.size() > 0){
                notifyBackPressListener();
                return true;
            }else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public interface OnBackPressListener{
        public void onBackPress();
    }

    public interface OnActivityResult{
        public void onActivityResult( ActivityResultModel obj);
    }
}
