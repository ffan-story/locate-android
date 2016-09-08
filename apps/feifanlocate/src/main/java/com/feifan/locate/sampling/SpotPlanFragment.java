package com.feifan.locate.sampling;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.R;
import com.feifan.locate.ToolbarActivity;
import com.feifan.locate.utils.NumberUtils;
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;
import com.feifan.locate.widget.plan.IPlanLayer.OnOperationListener;
import com.feifan.locate.widget.plan.IPlanPoint;
import com.feifan.locate.widget.plan.MarkLayer;
import com.feifan.locate.widget.plan.MarkLayer.MarkPoint;
import com.feifan.locate.widget.plan.PlanView;
import com.feifan.locate.widget.plan.PlanView.OnPlanListener;
import com.feifan.locate.widget.popup.BubbleMenu;
import com.feifan.locate.widget.ui.AbsSensorFragment;
import com.feifan.locate.provider.LocateData.SampleSpot;
import com.feifan.locate.provider.LocateData.WorkSpot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpotPlanFragment extends AbsSensorFragment implements OnPlanListener, OnOperationListener {

    public static final String EXTRA_KEY_ZONE = "zone";

    private static final int REQUEST_CODE_DETAIL = 0;

    private Intent detailIntent;
    private Bundle args;

    private PlanView plan;
    private MarkLayer markLayer;
    private BubbleMenu menu;
    private int transY;

    // data
    private int zoneId;
//    private int workSpotId;
    private float radian;
    private static final int LOADER_ID = 2;   // 使用异步加载组件时分配的ID，不能与其他数据使用的ID相同

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
        try {
            // get input stream
            InputStream ims = getContext().getAssets().open("zone_2.png");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            plan.setImageDrawable(d);
            plan.setPlanListener(this);
        }
        catch(IOException ex) {
            return;
        }

        // 添加标记层
        markLayer = new MarkLayer();
        Bitmap bmpMark = BitmapFactory.decodeResource(getResources(), R.mipmap.plan_mark);
        markLayer.setMarkIcon(bmpMark);
        markLayer.setOperationListener(this);
        plan.addLayer(markLayer);

        ImageView control = findView(R.id.spot_plan_lock);
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                plan.lock();
            }
        });

        ImageView clear = findView(R.id.spot_plan_clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //plan
            }
        });

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

        // temp
        TextView board = findView(R.id.board);
        Cursor data = WorkSpot.findAll(getContext());
        String value = "";
        if(data.getCount() != 0) {
            while(data.moveToNext()) {
                int xIndex = data.getColumnIndexOrThrow(WorkSpot.X);
                float x = data.getFloat(xIndex);
                int yIndex = data.getColumnIndexOrThrow(WorkSpot.Y);
                float y = data.getFloat(yIndex);
                int zoneIndex = data.getColumnIndexOrThrow(WorkSpot.ZONE);
                int zone = data.getInt(zoneIndex);
                value += "(" + x + "," + y + "," + zone + ")\n";
            }
            data.close();
        }
        board.setText(value);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_DETAIL:
                if(resultCode == Activity.RESULT_OK) {
                    // 删除标记点
                    MarkLayer.MarkPoint mark = data.getParcelableExtra(SpotDetailFragment.EXTRA_KEY_MARKPOINT);
                    markLayer.remove(mark);
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
        return LOADER_ID;
    }

    @Override
    protected Uri getContentUri() {
        return WorkSpot.CONTENT_URI;
    }

    @Override
    protected <A extends RecyclerCursorAdapter> A getAdapter() {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        loadSpots(data);
        // 停止加载
        loader.stopLoading();
    }

    private void loadSpots(Cursor data) {
        LogUtils.i("load " + data.getCount() + " work spots data");
        List<MarkPoint> marks = new ArrayList<MarkPoint>();
        if(data != null) {
            while(data.moveToNext()) {
                int xIndex = data.getColumnIndexOrThrow(WorkSpot.X);
                float x = data.getFloat(xIndex);
                int yIndex = data.getColumnIndexOrThrow(WorkSpot.Y);
                float y = data.getFloat(yIndex);
                marks.add(new MarkPoint(0, 0, x, y));
            }
            data.close();
            markLayer.setPendingData(marks);
        }
    }

    @Override
    public void onCreatePoint(IPlanPoint point) {
        float roundX = NumberUtils.round(point.getRawX(), 4);
        float roundY = NumberUtils.round(point.getRawY(), 4);
        point.setRaw(roundX, roundY);
        WorkSpot.add(getContext(), roundX, roundY, zoneId);
    }

    @Override
    public void onDeletePoint(IPlanPoint point) {
        WorkSpot.remove(getContext(), point.getRawX(), point.getRawY(), zoneId);
    }

    @Override
    public void onPress(IPlanPoint mark, float x, float y) {
        if(transY == 0) {
            // todo 使用mark的高度更为合理
            transY = ((AppCompatActivity)getActivity()).getSupportActionBar().getHeight() - 48;
        }
        menu.show(plan, (int)x, (int)y + transY);
    }

    @Override
    public void onLongPress(IPlanPoint mark, float x, float y) {
        // 获取坐标点的ID
        int workspotId = -1;
        Cursor pointCursor = WorkSpot.find(getContext(), mark.getRawX(), mark.getRawY(), zoneId);
        if(pointCursor != null && pointCursor.getCount() == 1) {
            pointCursor.moveToFirst();
            int idIndex = pointCursor.getColumnIndexOrThrow(WorkSpot._ID);
            workspotId = pointCursor.getInt(idIndex);
        }
        if(workspotId == -1) {
            Toast.makeText(getContext(), "(" + mark.getRawX() + "," + mark.getRawY() + ") not found", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // 添加样本点到数据库
            SampleSpot.add(getContext(), mark.getRawX(), mark.getRawY(), radian, workspotId);

            // 打开详情界面
            args.putParcelable(SpotDetailFragment.EXTRA_KEY_MARKPOINT, (Parcelable) mark);
            args.putString(LOADER_KEY_SELECTION, "workspot=?");
            args.putStringArray(LOADER_KEY_SELECTION_ARGS, new String[]{ String.valueOf(workspotId) });
            startActivityForResult(detailIntent, REQUEST_CODE_DETAIL);
        }

    }

    @Override
    public void onAdjustEnd(IPlanPoint point) {

    }

    @Override
    public void onNop(float x, float y) {
        Toast.makeText(getContext(), "(" + x + "," + y + ") is out of range", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onOrientationChanged(float radian) {
        // TODO 更新界面
        this.radian = radian;
    }
}
