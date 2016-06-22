package com.libs.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.libs.ui.R;
import com.libs.ui.activities.BaseActivity;


public abstract class RootFragment<T> extends Fragment implements LoaderCallbacks<T> {

    protected ViewGroup mContainerView;
    protected ViewGroup mLoadingView;
    protected ViewGroup mEmptyView;
    protected View mActionBarView;
    protected ActionBar mAbar;
    protected Toolbar mToolBar;
    private LoaderManager mLoaderManager;
    protected AbsToolBarDelegate mToolBarDelegate;
    private final int ACTINBAR_ID = 0xff01;
    private final int LOADER_LOCAL_ID = 0xff02;
//    private final int CUSTON_VIEW_TAG = R.string.app_name;

    /**
     * receiver msg from activity
     *
     * @param object
     */
    public abstract void onActivityEventPost(Object object);

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    protected View onActionRightView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    /**
     * create loading view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    protected abstract View onCreateLoadingView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * create empty view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    protected abstract View onCreateEmptyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);


    /**
     * if not show actionbar in fragment ,please set return null;
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    protected View onCreateActionBarView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActionBarView = inflater.inflate(R.layout.common_fragment_actionbar_view, container, false);
        return null;
    }

    protected void onActionBarCreated(){
        Toolbar toolbar = (Toolbar) mActionBarView.findViewById(R.id.toolbar);
        ((BaseActivity) getActivity()).setSupportActionBar(toolbar);
        mAbar = ((BaseActivity) getActivity()).getSupportActionBar();
    }

    /**
     * define user's custom view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public abstract View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected AbsToolBarDelegate onCustomToolBarCreated(){
        return null;
    }
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout root;
        mToolBarDelegate = onCustomToolBarCreated();
        if(mToolBarDelegate == null) {
            mToolBarDelegate = new ToolBarDelegate(getActivity());
        }
        if(isCustomView()){
            root = (RelativeLayout) inflater.inflate(R.layout.common_root_custom_fragment_layout, container, false);
        }else {
            root = (RelativeLayout) inflater.inflate(R.layout.common_root_fragment_layout, container, false);
            mActionBarView = root.findViewById(R.id.action_bar);
            initActionBar();
        }
        View actionBarRightView = onActionRightView(inflater,mToolBarDelegate.getExtraView(),savedInstanceState);
        if(actionBarRightView != null) {
            mToolBarDelegate.addRightView(actionBarRightView);
        }
        ViewGroup root_content = (ViewGroup) root.findViewById(R.id.content);
        RelativeLayout content = (RelativeLayout) inflater.inflate(R.layout.common_custom_fragment_layout, root_content, false);
        root_content.addView(content);
        mContainerView = (ViewGroup) content.findViewById(R.id.container);
        View content_content = onCreateCustomView(inflater, mContainerView, savedInstanceState);
        if (content_content != null) {
            mContainerView.addView(content_content);
        }
        if(isCustomView()){
        ViewGroup viewGroup = (ViewGroup) content.findViewWithTag(getString(R.string.app_name));
            mLoadingView = (ViewGroup) onCreateLoadingView(inflater, mLoadingView, savedInstanceState);
            if (mLoadingView != null) {
                viewGroup.addView(mLoadingView);
            }
            mEmptyView = (ViewGroup) onCreateEmptyView(inflater, mEmptyView, savedInstanceState);
            if (mEmptyView != null) {
                viewGroup.addView(mEmptyView);
            }
        }else {
            mLoadingView = (ViewGroup) content.findViewById(R.id.loading);
            View content_loading = onCreateLoadingView(inflater, mLoadingView, savedInstanceState);
            if (content_loading != null) {
                mLoadingView.addView(content_loading);
            }
            mEmptyView = (ViewGroup) content.findViewById(R.id.empty);
            View content_empView = onCreateEmptyView(inflater, mEmptyView, savedInstanceState);
            if (content_empView != null) {
                mEmptyView.addView(content_empView);
            }
        }
        return root;
    }

    protected boolean isCustomView(){
        return false;
    }
    /**
     *
     * @param res
     */
    protected void setCustomTitle(int res) {
        setCustomTitle(getString(res));
    }

    /**
     *
     * @param res
     */
    protected void setTitle(int res) {
        setTitle(getString(res));
    }

    /**
     *
     * @param title
     */
    protected void setTitle(String title) {
        TextView titleView = (TextView) mActionBarView.findViewById(R.id.tool_title);
        titleView.setText(title);
    }

    private void initActionBar(){
        mToolBar = (Toolbar) mActionBarView.findViewById(R.id.toolbar);
        ((BaseActivity) getActivity()).setSupportActionBar(mToolBar);
        mAbar = ((BaseActivity) getActivity()).getSupportActionBar();
        mAbar.setCustomView(mToolBarDelegate.getContentView());
        mAbar.setDisplayShowCustomEnabled(true);
        mAbar.setDisplayHomeAsUpEnabled(true);
        mAbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mToolBarDelegate.getBackView().setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }
    /**
     *
     * @param title
     */
    protected void setCustomTitle(String title) {
        if(!TextUtils.isEmpty(title) && mToolBarDelegate.getTitleView() != null){
            ((TextView)mToolBarDelegate.getTitleView()).setText(title);
        }
    }
    /**
     * 调用加载本地数据的loader，然后重写子类的方法onCreateLoader onLoadFinished onLoaderReset
     */
    public void startLoadLocal(Bundle args) {
        if (mLoaderManager == null) {
            mLoaderManager = getLoaderManager();
        }
        int localid = getLocalLoadId();
        if(localid == 0){
            localid = LOADER_LOCAL_ID;
        }
        mLoaderManager.initLoader(localid, args, this);
    }

    protected int getLocalLoadId(){
        return 0;
    }
    protected View getLoadingView() {
        return mLoadingView;
    }

    protected View getEmptyView() {
        return mEmptyView;
    }

    protected View getContainerView() {
        return mContainerView;
    }

    public void onBackPressed() {
        if(getActivity() != null){
            getActivity().finish();
            if(getActivity().getSupportFragmentManager().getBackStackEntryCount() <= 1){
                getActivity().finish();
            }else{
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    /**
     * 再添加的时候，需要重写继承与localloader的方法
     */
    @Override
    public Loader<T> onCreateLoader(int id, Bundle arg1) {
        return onCustomLoader(id,arg1);
    }

    protected Loader<T> onCustomLoader(int id, Bundle arg1){
        return null;
    }

    @Override
    public void onLoadFinished(Loader<T> arg0, T t) {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoadingView = null;
        mEmptyView = null;
        mContainerView = null;
        mLoaderManager = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
