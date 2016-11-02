package com.feifan.locate.sampling.workline;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Toast;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.Constants;
import com.feifan.locate.R;
import com.feifan.locate.provider.LocateData.LineSpot;
import com.feifan.locate.provider.LocateData.WorkLine;
import com.feifan.locate.sampling.SampleDetailFragment;
import com.feifan.locate.sampling.SamplePlanFragment;
import com.feifan.locate.utils.DataUtils;
import com.feifan.locate.utils.NumberUtils;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.locate.widget.popup.BubbleMenu;
import com.feifan.maplib.entity.ILayerLine;
import com.feifan.maplib.entity.ILayerLineImpl;
import com.feifan.maplib.layer.LineLayer;
import com.feifan.maplib.layer.OnOperationCallback;
import com.feifan.maplib.entity.ILayerPoint;
import com.feifan.maplib.entity.ILayerPointImpl;
import com.feifan.maplib.entity.LinePoint;
import com.rtm.frm.data.Location;
import com.rtm.frm.map.MapView;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuchunlei on 2016/10/26.
 */

public class LinePlanFragment extends SamplePlanFragment implements OnOperationCallback<LinePoint> {

    // menu
    @IdRes
    private static final int ID_MENU_CONFIRM = 1;
    @IdRes
    private static final int ID_MENU_CANCEL = 2;

    // draw
    private LineLayer mLayer;

    // line
    private LinePoint pointOne; // 起点
    private LinePoint pointTwo; // 终点, 终点用于下一条线段的起点
    private ILayerLine mCurrentLine = new ILayerLineImpl();
    private LineInfo mLineInfo = new LineInfo();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDetailFragment(LineDetailFragment.class.getName());
        mParams.put(SampleDetailFragment.EXTRA_KEY_WORK, mLineInfo);
        mParams.put(LOADER_KEY_SELECTION, "workline=?");
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
                            }else {
                                mCurrentLine.setPointTwo(pointTwo);
                                pointOne.add(pointTwo);
                                pointTwo.add(pointOne);
                                // 保存路线到数据库
                                int id = WorkLine.add(getContext(), mCurrentLine.getPointOne().getId(),
                                        mCurrentLine.getPointTwo().getId(), getZone().id);
                                mCurrentLine.setId(id);
                                mLinesMap.put(new LineKey(pointOne.getId(), pointTwo.getId()), id);

                                updateLineInfo(mCurrentLine);
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
    protected void onCreateLayer(MapView mapView) {

        mLayer = new LineLayer(mapView);
        final Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.plan_mark);
        mLayer.setIcon(icon);
        mLayer.setCallback(this);

        mapView.addMapLayer(mLayer);
    }

    @Override
    public void onResume() {
        super.onResume();
        ILayerPoint point = getCurrentPoint();
        if(point != null) {
            showMenu(point.getDraw().getX(), point.getDraw().getY());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_DETAIL:
                if(resultCode == Activity.RESULT_OK) {
                    // 删除标记点
                    final LineInfo info = data.getParcelableExtra(SampleDetailFragment.EXTRA_KEY_WORK);
                    updateLayerLine(info);
                    if(mLayer.remove(mCurrentLine)) {
                        WorkLine.remove(getContext(), mCurrentLine.getId());
                        mLinesMap.remove(new LineKey(info.pointOneId, info.pointTwoId));

                        // 重置
                        mCurrentLine.setPointOne(null);
                        mCurrentLine.setPointTwo(null);
                        mCurrentLine.setId(0);
                    }
                }
                break;
        }
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
    public boolean onDeletePoint(LinePoint point) {
        // 删除关联文件 TODO 在数据库中保存和获取文件地址

        return LineSpot.remove(getContext(), point.getId());
    }

    @Override
    public void onPressPoint(LinePoint point, PointInfo rawInfo) {
        resetCurrent();
        LogUtils.e(point.toString() + point.getClass().getName());
        pointOne = point;
        showMenu(point.getDraw().getX(), point.getDraw().getY());
    }

    @Override
    public void onLongPressPoint(LinePoint point) {
        if(point.getEdge().size() != 0) {
            LinePoint point2 = point.getEdge().get(0);
            int lineId = mLinesMap.get(new LineKey(point.getId(), point2.getId()));
            mCurrentLine.setId(lineId);
            mCurrentLine.setPointOne(point);
            mCurrentLine.setPointTwo(point2);
            updateLineInfo(mCurrentLine);
            mParams.put(LOADER_KEY_SELECTION_ARGS, new String[]{ String.valueOf(mCurrentLine.getId())});
            showDetail(mParams);
        }
    }

    @Override
    public void onAdjustEnd() {
        final ILayerPoint point = getCurrentPoint();
        if(point != null) {
            LineSpot.update(getContext(), point.getLocation().getX(), point.getLocation().getY(), point.getId());
            if(point.isMovable()) {
                showMenu(point.getDraw().getX(), point.getDraw().getY());
            }
        }
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
                LinePoint pointOne = new LinePoint(oneId);
                if(!pointsSet.contains(pointOne)) {
                    Location l1 = new Location(oneX, oneY);
                    pointOne.setLocation(l1);
                    points.add(pointOne);
                    pointsSet.add(pointOne);
                }else {
                    int indexOne = points.indexOf(pointOne);
                    pointOne = points.get(indexOne);
                }

                LinePoint pointTwo = new LinePoint(twoId);
                if(!pointsSet.contains(pointTwo)) {
                    Location l2 = new Location(twoX, twoY);
                    pointTwo.setLocation(l2);
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

    private LinePoint createPoint(float x, float y) {
        LinePoint point;
        float roundX = NumberUtils.round(x, 4);
        float roundY = NumberUtils.round(y, 4);
        // 创建时把绘制坐标当作实际坐标
        int id = LineSpot.add(getContext(), roundX, roundY, getZone().id);
        point = new LinePoint(id, roundX, roundY);
        point.setMovable(true);
        return point;
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

    // 获取当前正在操作的点
    private LinePoint getCurrentPoint() {
        return pointTwo == null ? pointOne : pointTwo;
    }

    private void updateLineInfo(ILayerLine line) {
        mLineInfo.id = line.getId();
        mLineInfo.pointOneId = line.getPointOne().getId();
        mLineInfo.pointOneX = line.getPointOne().getLocation().getX();
        mLineInfo.pointOneY = -line.getPointOne().getLocation().getY();
        mLineInfo.pointTwoId = line.getPointTwo().getId();
        mLineInfo.pointTwoX = line.getPointTwo().getLocation().getX();
        mLineInfo.pointTwoY = -line.getPointTwo().getLocation().getY();
    }

    private void updateLayerLine(LineInfo info) {
        mCurrentLine.setId(info.id);
        LinePoint pointOne = new LinePoint(info.pointOneId);
        LinePoint pointTwo = new LinePoint(info.pointTwoId);
        pointOne.add(pointTwo);
        pointTwo.add(pointOne);
    }

    private void resetCurrent() {
        pointOne = null;
        pointTwo = null;
        mCurrentLine.setPointOne(null);
        mCurrentLine.setPointTwo(null);
    }
}
