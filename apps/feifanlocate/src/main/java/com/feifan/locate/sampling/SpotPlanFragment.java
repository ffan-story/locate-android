package com.feifan.locate.sampling;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.feifan.locate.R;
import com.feifan.locate.ToolbarActivity;
import com.feifan.locate.utils.LogUtils;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;
import com.feifan.locate.widget.plan.LocationLayer;
import com.feifan.locate.widget.plan.MarkLayer;
import com.feifan.locate.widget.plan.OnMarkTouchListener;
import com.feifan.locate.widget.plan.PlanView;
import com.feifan.locate.widget.popup.BubbleMenu;
import com.feifan.locate.widget.ui.AbsSensorFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpotPlanFragment extends AbsSensorFragment implements OnMarkTouchListener {

    private static final int REQUEST_CODE_DETAIL = 0;
    private Intent detailIntent;
    private Bundle args;

    private PlanView plan;
    private LocationLayer locationLayer;
    private BubbleMenu menu;
    private int transY;

    public SpotPlanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        detailIntent = new Intent(getContext().getApplicationContext(), ToolbarActivity.class);
        detailIntent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, SpotDetailFragment.class.getName());
        args = new Bundle();
        detailIntent.putExtra(ToolbarActivity.EXTRA_KEY_ARGUMENTS, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_spot_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化PlanView
        plan = findView(R.id.spot_plan_img);
        locationLayer = new LocationLayer();
        plan.addLayer(locationLayer);

        ArrayList<String> items = new ArrayList<String>();
        items.add("采样");
        items.add("详情");
        items.add("设置");
        menu = new BubbleMenu(getContext(), items, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case 1:
                        LogUtils.e("click 采样");
                        break;
                    case 2:
                        LogUtils.e("click 详情");
                        break;
                    case 3:
                        LogUtils.e("click 设置");
                        break;
                }
                menu.dismiss();
            }
        });

        try {
            // get input stream
            InputStream ims = getContext().getAssets().open("zone_2.png");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            plan.setImageDrawable(d);
            plan.setMarkListener(this);
        }
        catch(IOException ex) {
            return;
        }

        ImageView control = findView(R.id.spot_plan_control);
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plan.lock();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_DETAIL:
                if(resultCode == Activity.RESULT_OK) {
                    // 删除标记点
                    MarkLayer.MarkPoint mark = data.getParcelableExtra(SpotDetailFragment.EXTRA_KEY_MARKPOINT);
                    boolean ret = plan.getMarkLayer().removeMark(mark);
                }
                break;
        }
    }

    @Override
    protected int getTitleResource() {
        return R.string.spot_plan_title_text;
    }

    @Override
    protected int getLoaderId() {
        return 0;
    }

    @Override
    protected Uri getContentUri() {
        return null;
    }

    @Override
    protected <A extends RecyclerCursorAdapter> A getAdapter() {
        return null;
    }

    @Override
    public void onPress(MarkLayer.MarkPoint mark, float x, float y) {
        if(transY == 0) {
            // todo 使用mark的高度更为合理
            transY = ((AppCompatActivity)getActivity()).getSupportActionBar().getHeight() - 48;
        }
        menu.show(plan, (int)x, (int)y + transY);
    }

    @Override
    public void onLongPress(MarkLayer.MarkPoint mark, float x, float y) {
        args.putParcelable(SpotDetailFragment.EXTRA_KEY_MARKPOINT, mark);
        startActivityForResult(detailIntent, REQUEST_CODE_DETAIL);
    }

    @Override
    protected void onOrientationChanged(float radian) {
        // TODO 更新界面
    }
}
