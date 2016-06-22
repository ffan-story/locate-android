package com.libs.ui.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.libs.ui.R;

/**
 * Created by mengmeng on 15/12/1.
 */
public class ToolBarDelegate extends AbsToolBarDelegate{
    /*上下文，创建view的时候需要用到*/
    private Context mContext;

    /*base view*/
    private View mContentView;

    private View mBackView;

    private FrameLayout mExtraView;

    /*视图构造器*/
    private LayoutInflater mInflater;

    public ToolBarDelegate(Context context) {
        super(context);
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mContentView = mInflater.inflate(R.layout.common_custom_toolbar_layout,null);
    }

    public View getContentView(){
        return mContentView;
    }
    public ImageView getBackView(){
        return (ImageView) mContentView.findViewById(R.id.backview);
    }
    public TextView getTitleView(){
        return (TextView) mContentView.findViewById(R.id.title);
    }


//    protected FrameLayout getExtraView(){
//        return (FrameLayout) mContentView.findViewById(R.id.extra);
//    }


    @Override
    public ViewGroup getExtraView() {
        return (FrameLayout) mContentView.findViewById(R.id.extra);
    }

    public void addRightView(View view){
        if(view != null) {
            getExtraView().addView(view);
        }
    }
}
