package com.feifan.sampling.map;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.feifan.sampling.R;
import com.libs.ui.fragments.CommonFragment;
import com.libs.ui.views.photoview.PhotoView;
import com.libs.ui.views.photoview.PhotoViewAttacher;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mengmeng on 16/7/4.
 */
public class MapFragment extends CommonFragment {
    private PhotoView mPhotoview;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private ImageView mImageView;
    private MapDialogFragment mDialogFragment;
    private float mParam_x;
    private float mParam_y;
    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPhotoview = (PhotoView) inflater.inflate(R.layout.biz_map_fragment_layout,container,false);
        mImageView = (ImageView) inflater.inflate(R.layout.biz_map_fragment_tap,null);
        mDialogFragment = new MapDialogFragment();
        return mPhotoview;
    }


    private void setImag(){
        AssetManager assetManager = getResources().getAssets();
        InputStream is = null;
        try {
            is = assetManager.open("map.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //以下注释掉的代码不靠谱.若采用,会有异常
        //InputStream is = assetManager.open("file:///android_asset/Fresh_01.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        mPhotoview.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mPhotoview != null){
            setImag();
            mPhotoview.setMinimumScale(0.5f);
            mPhotoview.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                /**
                 * A callback to receive where the user taps on a photo. You will only
                 * receive a callback if the user taps on the actual photo, tapping on
                 * 'whitespace' will be ignored.
                 *
                 * @param view - View the user tapped.
                 * @param x    - where the user tapped from the of the Drawable, as
                 *             percentage of the Drawable width.
                 * @param y    - where the user tapped from the top of the Drawable, as
                 */
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    double dl = 0.021;
                    Log.e("the tap position is: ",x*view.getWidth()*dl+"  ____   "+y*view.getHeight()*dl);
//                    mParam_x = (float) (x*view.getWidth()*dl);
//                    mParam_y = (float) (y*view.getHeight()*dl);
                }

                /**
                 * A callback to receive where the user taps on a photo. You will only
                 * receive a callback if the user taps on the actual photo, tapping on
                 * 'whitespace' will be ignored.
                 *
                 * @param view - View the user tapped.
                 * @param x    - where the user tapped from the of the Drawable, as
                 *             percentage of the Drawable width.
                 * @param y    - where the user tapped from the top of the Drawable, as
                 */
                @Override
                public void onRawPhotoTap(View view, float x, float y) {
                    double dl = 0.021;
                    float scale = mPhotoview.getScale();
                    Log.e("the raw position is: ",x * dl * scale+"____"+y * dl * scale);
                    mParam_x = (float) (x * dl * scale);
                    mParam_y = (float) (y * dl * scale);
                }

                @Override
                public void onViewTap(View view, float x, float y) {
                    createFloatView(x,y);
                }
            });
            mPhotoview.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                /**
                 * A callback to receive where the user taps on a ImageView. You will
                 * receive a callback if the user taps anywhere on the view, tapping on
                 * 'whitespace' will not be ignored.
                 *
                 * @param view - View the user tapped.
                 * @param x    - where the user tapped from the left of the View.
                 * @param y    - where the user tapped from the top of the View.
                 */
                @Override
                public void onViewTap(View view, float x, float y) {
                    createFloatView(x,y);
                }
            });
        }
    }

    private void createFloatView(float x, float y){
        if(mWindowManager != null) {
            mWindowManager.removeViewImmediate(mImageView);
        }else {
            //获取LayoutParams对象
            mParams = new WindowManager.LayoutParams();

            //获取的是LocalWindowManager对象
            mWindowManager = getActivity().getWindowManager();
            //获取的是CompatModeWrapper对象
            //mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            mParams.format = PixelFormat.RGBA_8888;
            ;
            mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mParams.gravity = Gravity.LEFT | Gravity.TOP;
            mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        Drawable bitmap = getResources().getDrawable(R.drawable.biz_map_loc_mark);
//        mParams.x = (int) (x - bitmap.getMinimumWidth()/2);
//        mParams.y = (int) (y - bitmap.getMinimumWidth());
        mParams.x = (int) (x - bitmap.getMinimumWidth()/2);
        mParams.y = (int) (y+bitmap.getMinimumHeight()/2);
        Log.e("createFloatView ",x+" _ "+y+" _ "+bitmap.getMinimumWidth()/2+" _ "+bitmap.getMinimumWidth());
        Log.e("params ",mParams.x+" _ "+mParams.y);
        mWindowManager.addView(mImageView, mParams);
        Bundle args = new Bundle();
        args.putFloat("param_x",mParam_x);
        args.putFloat("param_y",mParam_y);
        mDialogFragment.setArguments(args);
        mDialogFragment.setTargetFragment(this,0);
        mDialogFragment.setTargetFragment(MapFragment.this, 0);
        mDialogFragment.show(getFragmentManager(), MapFragment.class.getName());
    }
}
