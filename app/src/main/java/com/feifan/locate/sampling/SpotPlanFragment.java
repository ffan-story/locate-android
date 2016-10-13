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
import android.widget.Toast;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.Constants;
import com.feifan.locate.R;
import com.feifan.locate.ToolbarActivity;
import com.feifan.locate.utils.IOUtils;
import com.feifan.locate.utils.NumberUtils;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;
import com.feifan.locate.widget.popup.BubbleMenu;
import com.feifan.locate.widget.ui.AbsSensorFragment;
import com.feifan.locate.provider.LocateData.SampleSpot;
import com.feifan.locate.provider.LocateData.WorkSpot;
import com.feifan.locate.sampling.model.ZoneModel;
import com.feifan.planlib.PlanView;
import com.feifan.planlib.PlanView.OnPlanListener;
import com.feifan.planlib.ILayerPoint;
import com.feifan.planlib.OnOperationListener;
import com.feifan.planlib.layer.MarkLayer;
import com.feifan.planlib.layer.MarkPoint;

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

    // plan
    private PlanView plan;
    private MarkLayer markLayer;
    private BubbleMenu menu;
    private int transY;
    private ILayerPoint currentPoint;

    // data
    private ZoneModel zone;
    private float degree;
    private static final int LOADER_ID = 2;   // 使用异步加载组件时分配的ID，不能与其他数据使用的ID相同

    public SpotPlanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        zone = getArguments().getParcelable(EXTRA_KEY_ZONE);

        detailIntent = new Intent(getContext().getApplicationContext(), ToolbarActivity.class);
//        detailIntent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, SpotDetailFragment.class.getName());
        // FIXME temp for test
        detailIntent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, LineDetailFragment.class.getName());
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
        plan.setPlanScale(zone.scale);
        InputStream ims = null;
        try {
            ims = getContext().getAssets().open(zone.plan);
            Drawable d = Drawable.createFromStream(ims, null);
            plan.setImageDrawable(d);
            plan.setPlanListener(this);
        }
        catch(IOException ex) {
            return;
        }finally {
            IOUtils.closeQuietly(ims);
        }

        // 添加标记层
        markLayer = new MarkLayer();
        Bitmap bmpMark = BitmapFactory.decodeResource(getResources(), R.mipmap.plan_mark);
        markLayer.setMarkIcon(bmpMark);
        markLayer.setOperationListener(this);
        plan.addLayer(markLayer);

        String[] items = getResources().getStringArray(R.array.menu);
        menu = new BubbleMenu(getContext(), items, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case 1:
                        if(currentPoint != null && currentPoint instanceof MarkPoint) {
                            ((MarkPoint)currentPoint).setLocked(true);
                            doAddPointAndShowDetails(currentPoint);
                        }

                        break;
//                    case 2:
//                        LogUtils.e("click 详情");
//                        break;
//                    case 3:
//                        LogUtils.e("click 设置");
//                        break;
                }
                menu.dismiss();
            }
        });

        // temp
//        TextView board = findView(R.id.board);
//        Cursor data = WorkSpot.findAll(getContext());
//        String value = "";
//        if(data.getCount() != 0) {
//            while(data.moveToNext()) {
//                int xIndex = data.getColumnIndexOrThrow(WorkSpot.X);
//                float x = data.getFloat(xIndex);
//                int yIndex = data.getColumnIndexOrThrow(WorkSpot.Y);
//                float y = data.getFloat(yIndex);
//                int zoneIndex = data.getColumnIndexOrThrow(WorkSpot.ZONE);
//                int zone = data.getInt(zoneIndex);
//                value += "(" + x + "," + y + "," + zone + ")\n";
//            }
//            data.close();
//        }
//        board.setText(value);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(currentPoint == null) { //被删除
            menu.dismiss();
        }
        plan.invalidate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_DETAIL:
                if(resultCode == Activity.RESULT_OK) {
                    // 删除标记点
                    MarkPoint mark = data.getParcelableExtra(SpotDetailFragment.EXTRA_KEY_MARKPOINT);
                    if(markLayer.remove(mark)) {
                        currentPoint = null;
                    }
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
    protected <A extends ICursorAdapter> A getAdapter() {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        loadSpots(data);
        // 调用此方法表示只加载一次
        loader.stopLoading();
    }

    private void loadSpots(Cursor data) {
        LogUtils.i("load " + data.getCount() + " work spots data");
        if(data != null && data.getCount() != 0) {
            List<MarkPoint> marks = new ArrayList<MarkPoint>();
            while(data.moveToNext()) {
                int idIndex = data.getColumnIndexOrThrow(WorkSpot._ID);
                int id = data.getInt(idIndex);
                int xIndex = data.getColumnIndexOrThrow(WorkSpot.X);
                float x = data.getFloat(xIndex);
                int yIndex = data.getColumnIndexOrThrow(WorkSpot.Y);
                float y = data.getFloat(yIndex);
                int movableIndex = data.getColumnIndexOrThrow(WorkSpot.MOVABLE);
                boolean movable = data.getInt(movableIndex) == 1;
                MarkPoint point = new MarkPoint(0, 0, x, y, zone.scale);
                point.setLocked(!movable);
                point.setId(id);
                marks.add(point);
            }
            markLayer.setPendingData(marks);
            data.close();
        }
    }

    @Override
    public ILayerPoint onCreatePoint(PointInfo info) {
        ILayerPoint retPoint = null;
        Cursor cursor = WorkSpot.findMovableSpot(getContext(), zone.id);
        if(cursor.getCount() == 0) { // 图中没有可移动点
            // 添加到数据库
            float roundX = NumberUtils.round(info.x, 4);
            float roundY = NumberUtils.round(info.y, 4);
            int id = WorkSpot.add(getContext(), roundX, roundY, zone.id);

            // 创建坐标点
            retPoint = new MarkPoint(id, roundX, roundY);
            currentPoint = retPoint;
        }else {
            if(currentPoint != null) {
                Toast.makeText(getContext(), "handle spot(" + currentPoint.getRawX() + "," + currentPoint.getRawY() + ") first",
                        Toast.LENGTH_SHORT).show();
            }
        }

        return retPoint;
    }

    @Override
    public boolean onDeletePoint(ILayerPoint point) {
        return WorkSpot.remove(getContext(), point.getId());
    }

    @Override
    public void onPress(ILayerPoint mark, float x, float y) {
        currentPoint = mark;
        showMenu(x, y);
    }

    @Override
    public void onLongPress(ILayerPoint mark, float x, float y) {
        currentPoint = mark;
        doAddPointAndShowDetails(mark);
    }

    @Override
    public void onAdjustEnd() {
        if(currentPoint != null) {
            WorkSpot.update(getContext(), currentPoint.getRawX(), currentPoint.getRawY(), currentPoint.getId());
            if(currentPoint instanceof MarkPoint) {
                if(!((MarkPoint)currentPoint).isLocked()) {
                    showMenu(currentPoint.getLocX(), currentPoint.getLocY());
                }
            }
        }
    }

    @Override
    public void onNop(float x, float y) {
        Toast.makeText(getContext(), "(" + x + "," + y + ") is out of range", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onOrientationChanged(float radian) {
        // TODO 更新界面
        this.degree = NumberUtils.degree(radian);
    }

    private void showMenu(float x, float y) {
        if(transY == 0) {
            // todo 使用mark的高度更为合理
            transY = ((AppCompatActivity)getActivity()).getSupportActionBar().getHeight() - 48;
        }
        menu.show(plan, (int)x, (int)y + transY);
    }

    private void doAddPointAndShowDetails(ILayerPoint mark) {
        int sampleId;
        Cursor cursor = SampleSpot.findByStatus(getContext(), SampleSpot.STATUS_READY, mark.getId());
        if(cursor.getCount() == 0) { // 不存在就绪状态的样本点
            // 添加样本点到数据库
            sampleId = SampleSpot.add(getContext(), mark.getRawX(), mark.getRawY(), degree, mark.getId());
        }else {
            if(cursor.getCount() > 1) {
                throw new IllegalStateException("error!there is more than 1 ready sample spot!");
            }else {
                // 更新样本点
                cursor.moveToFirst();
                int idIndex = cursor.getColumnIndexOrThrow(SampleSpot._ID);
                sampleId = cursor.getInt(idIndex);
            }
        }
        cursor.close();

        // 更新采集点状态为不可移动
        WorkSpot.update(getContext(), false, mark.getId());

        // 打开详情界面
        // query
        // FIXME enable me later
//        args.putString(LOADER_KEY_SELECTION, "workspot=?");
//        args.putStringArray(LOADER_KEY_SELECTION_ARGS, new String[]{ String.valueOf(mark.getId()) });

        // others
        args.putParcelable(SpotDetailFragment.EXTRA_KEY_MARKPOINT, (Parcelable) mark);
        args.putInt(SpotDetailFragment.EXTRA_KEY_SAMPLESPOT_ID, sampleId);
        args.putInt(SpotDetailFragment.EXTRA_KEY_FLOOR, zone.floorNo);
        args.putString(Constants.EXTRA_KEY_BUILDING, getArguments().getString(Constants.EXTRA_KEY_BUILDING));
        startActivityForResult(detailIntent, REQUEST_CODE_DETAIL);
    }
}
