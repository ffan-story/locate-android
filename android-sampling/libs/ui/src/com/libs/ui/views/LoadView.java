package com.libs.ui.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.libs.ui.R;


/**
 * Created by mengmeng on 15/4/1.
 */
public class LoadView extends FrameLayout{

    private ProgressBarCircular mProgress;
    private TextView mTextView;

    public LoadView(Context context) {
        super(context);
        initView();
    }

    public LoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.common_process_view,this,true);
        mProgress = (ProgressBarCircular) view.findViewById(R.id.progress);
        mTextView = (TextView) view.findViewById(R.id.desc);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        int size = width > height ? width:height;
        setMeasuredDimension(size, size);
    }


    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = widthSize;
                break;
        }
        return result;
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;

        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = heightSize;
                break;
        }
        return result;
    }

    /**
     *
     * @param res
     */
    public  void setDesc(int res){
        if(res > 0){
            String title = getContext().getString(res);
            setDesc(title);
        }
    }

    /**
     *
     * @param title
     */
    public  void setDesc(String title){
        if(!TextUtils.isEmpty(title)){
            mTextView.setText(title);
        }
    }
}
