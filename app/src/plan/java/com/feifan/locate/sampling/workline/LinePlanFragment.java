package com.feifan.locate.sampling.workline;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.Constants;
import com.feifan.locate.R;
import com.feifan.locate.provider.LocateData.LineSpot;
import com.feifan.locate.provider.LocateData.WorkLine;
import com.feifan.locate.sampling.SampleDetailFragment;
import com.feifan.locate.sampling.SamplePlanFragment;
import com.feifan.locate.sampling.workline.LineDetailFragment;
import com.feifan.locate.utils.NumberUtils;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.locate.widget.popup.BubbleMenu;
import com.feifan.planlib.ILayerLine;
import com.feifan.planlib.ILayerPoint;
import com.feifan.planlib.OnOperationCallback;
import com.feifan.planlib.PlanView;
import com.feifan.planlib.entity.IPlanLineImpl;
import com.feifan.planlib.entity.LinePoint;
import com.feifan.planlib.layer.LineLayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuchunlei on 16/10/14.
 */

public class LinePlanFragment extends SamplePlanFragment implements OnOperationCallback {

    // menu
    @IdRes
    private static final int ID_MENU_CONFIRM = 1;
    @IdRes
    private static final int ID_MENU_CANCEL = 2;

    // line
    private LineLayer mLayer;
    private LinePoint pointOne; // 起点
    private LinePoint pointTwo; // 终点, 终点用于下一条线段的起点
    private ILayerLine mCurrentLine = new IPlanLineImpl();
    private static class LineKey {
        int pointOneId;
        int pointTwoId;

        public LineKey(int oneId, int twoId) {
            pointOneId = oneId;
            pointTwoId = twoId;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof LineKey)) {
                return false;
            }
            LineKey key = (LineKey)o;
            return (pointOneId == key.pointOneId && pointTwoId == key.pointTwoId)
                    || (pointOneId == key.pointTwoId && pointTwoId == key.pointOneId);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + pointOneId + pointTwoId;
            return result;
        }
    }
    private Map<LineKey, Integer> mLinesMap = new HashMap<>();

    // intent
    private Map<String, Object> mParams = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDetailFragment(LineDetailFragment.class.getName());
        mParams.put(SampleDetailFragment.EXTRA_KEY_WORK, mCurrentLine);
        mParams.put(LOADER_KEY_SELECTION, "workline=?");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample_plan, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ILayerPoint point = getCurrentPoint();
        if(point != null) {
            showMenu(point.getLocX(), point.getLocY());
        }
    }

    @Override
    protected void initPlan(PlanView plan) {
        mLayer = new LineLayer();
        Bitmap bmpMark = BitmapFactory.decodeResource(getResources(), R.mipmap.plan_mark);
        mLayer.setIcon(bmpMark);
        mLayer.setCallback(this);
        plan.addLayer(mLayer);
    }

    @Override
    protected void onCreatePlanMenu(final BubbleMenu planMenu) {
        super.onCreatePlanMenu(planMenu);

        planMenu.setClosableOutside(false);
        planMenu.addItem(ID_MENU_CONFIRM, getString(R.string.line_plan_menu_title_confirm));
        planMenu.addItem(ID_MENU_CANCEL, getString(R.string.line_plan_menu_title_cancel));
        planMenu.setOnItemListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinePoint point = getCurrentPoint();
                switch (v.getId()) {
                    case ID_MENU_CONFIRM:
                        if(point != null) {
                            point.setMovable(false);
                            if(point == pointOne) {
                                mCurrentLine.setPointOne(pointOne);
//                                pointOne = null;
                            }else {
                                mCurrentLine.setPointTwo(pointTwo);
                                pointOne.add(pointTwo);
                                pointTwo.add(pointOne);
                                // 保存路线到数据库
                                int id = WorkLine.add(getContext(), mCurrentLine.getPointOne().getId(),
                                        mCurrentLine.getPointTwo().getId(), getZone().id);
                                mCurrentLine.setId(id);
                                mLinesMap.put(new LineKey(pointOne.getId(), pointTwo.getId()), id);

                                mParams.put(LOADER_KEY_SELECTION_ARGS, new String[]{ String.valueOf(mCurrentLine.getId())});
                                showDetail(mParams);

                                // 重置
                                pointOne = pointTwo; // 下一条路线的起点默认使用当前路线的终点
                                pointTwo = null;
                                mCurrentLine.setPointOne(pointOne);
                                mCurrentLine.setPointTwo(pointTwo);
                            }
                        }

                        break;
                    case ID_MENU_CANCEL:
                        if(point != null) {
                            clearPoint(point);
                            if(point.getEdge() != null && point.getEdge().isEmpty()) {
                                // 移除没有创建过边的点
                                LineSpot.remove(getContext(), point.getId());
                                mLayer.remove(point);
                            }
                        }

                        break;
                }
                planMenu.dismiss();
            }
        });
    }

    @Override
    protected int getLoaderId() {
        return Constants.LOADER_ID_WORKLINE;
    }

    @Override
    protected Uri getContentUri() {
        return WorkLine.CONTENT_URI;
    }

    @Override
    protected <A extends ICursorAdapter> A getAdapter() {

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        loadGraph(data);
        loader.stopLoading();
    }

    private void loadGraph(Cursor data) {
        LogUtils.i("load " + data.getCount() + " work spots data");
        if(data != null && data.getCount() != 0) {
            List<LinePoint> points = new LinkedList<>();
            Set<LinePoint> pointsSet = new HashSet<>();
            while(data.moveToNext()) {
                int idIndex = data.getColumnIndexOrThrow(WorkLine._ID);
                int id = data.getInt(idIndex);

                int oneIdIndex = data.getColumnIndexOrThrow("one_id");
                int oneId = data.getInt(oneIdIndex);
                int oneXIndex = data.getColumnIndexOrThrow("one_x");
                float oneX = data.getFloat(oneXIndex);
                int oneYIndex = data.getColumnIndexOrThrow("one_y");
                float oneY = data.getFloat(oneYIndex);

                int twoIdIndex = data.getColumnIndexOrThrow("two_id");
                int twoId = data.getInt(twoIdIndex);
                int twoXIndex = data.getColumnIndexOrThrow("two_x");
                float twoX = data.getFloat(twoXIndex);
                int twoYIndex = data.getColumnIndexOrThrow("two_y");
                float twoY = data.getFloat(twoYIndex);

                // 添加点集
                LinePoint pointOne = new LinePoint(0, 0, oneX, oneY, getZone().scale);
                pointOne.setId(oneId);
                if(!pointsSet.contains(pointOne)) {
                    points.add(pointOne);
                    pointsSet.add(pointOne);
                }else {
                    int indexOne = points.indexOf(pointOne);
                    pointOne = points.get(indexOne);
                }

                LinePoint pointTwo = new LinePoint(0, 0, twoX, twoY, getZone().scale);
                pointTwo.setId(twoId);
                if(!pointsSet.contains(pointTwo)) {
                    points.add(pointTwo);
                    pointsSet.add(pointTwo);
                } else {
                    int indexTwo = points.indexOf(pointTwo);
                    pointTwo = points.get(indexTwo);
                }

                // 添加边集
                pointOne.add(pointTwo);
                pointTwo.add(pointOne);

                // 添加到映射集合
                mLinesMap.put(new LineKey(pointOne.getId(), pointTwo.getId()), id);
            }
            mLayer.setPendingData(points);
            data.close();
        }
    }

    @Override
    public void onPress(ILayerPoint point, float x, float y) {
        resetCurrent();
        LogUtils.e(point.toString() + point.getClass().getName());
        final LinePoint lPoint = (LinePoint)point;
        pointOne = lPoint;
        showMenu(lPoint.getLocX(), lPoint.getLocY());
    }

    @Override
    public void onLongPress(ILayerPoint point, float x, float y) {
        final LinePoint lPoint1 = (LinePoint)point;
        if(lPoint1.getEdge().size() != 0) {
            LinePoint lPoint2 = lPoint1.getEdge().get(0);
            int lineId = mLinesMap.get(new LineKey(lPoint1.getId(), lPoint2.getId()));
            mCurrentLine.setId(lineId);
            mCurrentLine.setPointOne(lPoint1);
            mCurrentLine.setPointTwo(lPoint2);
            mParams.put(LOADER_KEY_SELECTION_ARGS, new String[]{ String.valueOf(mCurrentLine.getId())});
            showDetail(mParams);
        }
    }

    @Override
    public void onNop(float x, float y) {

    }

    @Override
    public ILayerPoint onCreatePoint(PointInfo info) {
        LinePoint retPoint = null;
        if(getMenu().isShown()) {
            Toast.makeText(getContext(), "confirm or cancel the point" + pointOne.toString(),
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        if(pointOne == null) {
            // 创建路线的第一个端点
            retPoint = createPoint(info.x, info.y);
            pointOne = retPoint;
        }else if(pointOne.isMovable()) {
            Toast.makeText(getContext(), "confirm the first point" + pointOne.toString(),
                    Toast.LENGTH_SHORT).show();
        }else if(pointTwo == null) {
            // 创建路线的第二个端点
            retPoint = createPoint(info.x, info.y);
            pointTwo = retPoint;
        }else if(pointTwo.isMovable()) {
            Toast.makeText(getContext(), "confirm the second point" + pointTwo.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        return retPoint;
    }

    @Override
    public boolean onDeletePoint(ILayerPoint point) {
        return true;
    }

    @Override
    public void onAdjustEnd() {

        final ILayerPoint point = getCurrentPoint();
        if(point != null) {
            LineSpot.update(getContext(), point.getRawX(), point.getRawY(), point.getId());
            if(point.isMovable()) {
                showMenu(point.getLocX(), point.getLocY());
            }
        }
    }

    private LinePoint createPoint(float x, float y) {
        LinePoint point;
        float roundX = NumberUtils.round(x, 4);
        float roundY = NumberUtils.round(y, 4);
        int id = LineSpot.add(getContext(), roundX, roundY, getZone().id);
        point = new LinePoint(id, roundX, roundY);
        return point;
    }

    // 获取当前正在操作的点
    private LinePoint getCurrentPoint() {
        return pointTwo == null ? pointOne : pointTwo;
    }

    private void clearPoint(ILayerPoint point) {
        if(point.equals(pointOne)) {
            pointOne = null;
        }
        if(point.equals(pointTwo)) {
            pointTwo = null;
        }
        mCurrentLine.clearPoint(point);
    }

    private void resetCurrent() {
        pointOne = null;
        pointTwo = null;
        mCurrentLine.setPointOne(null);
        mCurrentLine.setPointTwo(null);
    }
}
