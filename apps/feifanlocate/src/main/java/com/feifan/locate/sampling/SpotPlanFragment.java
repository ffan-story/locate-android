package com.feifan.locate.sampling;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;
import com.feifan.locate.widget.plan.LocationLayer;
import com.feifan.locate.widget.plan.MarkLayer;
import com.feifan.locate.widget.plan.OnMarkTouchListener;
import com.feifan.locate.widget.plan.PlanView;
import com.feifan.locate.widget.popup.BubbleMenu;
import com.feifan.locate.widget.ui.AbsSensorFragment;
import com.feifan.locate.provider.LocateData.SampleSpot;
import com.feifan.locate.provider.LocateData.WorkSpot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpotPlanFragment extends AbsSensorFragment implements OnMarkTouchListener {

    public static final String EXTRA_KEY_ZONE = "zone";

    private static final int REQUEST_CODE_DETAIL = 0;
    private Intent detailIntent;
    private Bundle args;

    private PlanView plan;
    private LocationLayer locationLayer;
    private BubbleMenu menu;
    private int transY;

    // data
    private int zoneId;
    private int workSpotId;
    private float radian;

    public SpotPlanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        zoneId = getArguments().getInt(EXTRA_KEY_ZONE);

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
//        locationLayer = new LocationLayer();
//        plan.addLayer(locationLayer);

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
    public void onCreateMark(MarkLayer.MarkPoint mark, float x, float y) {
        WorkSpot.addSpot(getContext(), mark.getOriginX(), mark.getOriginY(), zoneId);
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

        // 添加样本点到数据库
        SampleSpot.add(getContext(), mark.getOriginX(), mark.getOriginY(), radian, workSpotId);
    }

    @Override
    protected void onOrientationChanged(float radian) {
        // TODO 更新界面
        this.radian = radian;
    }
}
