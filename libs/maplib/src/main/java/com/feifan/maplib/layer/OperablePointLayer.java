package com.feifan.maplib.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.maplib.MapConstants;
import com.feifan.maplib.entity.ILayerPoint;
import com.feifan.maplib.layer.OnOperationCallback.PointInfo;
import com.rtm.frm.data.Location;
import com.rtm.frm.data.Point;
import com.rtm.frm.map.BaseMapLayer;
import com.rtm.frm.map.MapView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuchunlei on 2016/10/25.
 */

public abstract class OperablePointLayer<P extends ILayerPoint> extends BaseMapLayer {

    // map
    protected MapView mMapView;
    private List<P> mPoints = new ArrayList<>();

    // action
    private GestureDetector mGestureDetector;
    private P mCurrentPoint;
    private OnOperationCallback<P> mCallback;
    PointInfo mPointInfo = new PointInfo(-1, -1);

    // draw
    private SurfaceView mMapSurface;
    private Bitmap mIcon;
    private float mIconWidth;
    private float mIconHeight;
    private Paint mPaint = new Paint();
    private Rect mComputeRect = new Rect();

    public OperablePointLayer(MapView mapView) {
        initLayer(mapView);
        mPaint.setTextSize(28);
    }

    @Override
    public void initLayer(MapView mapView) {
        mMapView = mapView;
        mMapSurface = (SurfaceView) mMapView.getChildAt(0);
        mGestureDetector = new GestureDetector(mapView.getContext(), new LayerGestureListener());

    }

    @Override
    public boolean onTap(MotionEvent motionEvent) {
        mGestureDetector.onTouchEvent(motionEvent);

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentPoint = findPointByTouch(motionEvent.getX(), motionEvent.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if(mCurrentPoint == null) {
                    if(mCallback != null) {
                        mCallback.onAdjustEnd();
                    }
                }

                break;
        }
        return false;
    }

    @Override
    public void destroyLayer() {

    }

    @Override
    public void clearLayer() {

    }

    @Override
    public boolean hasData() {
        return false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mPoints != null && mPoints.size() != 0) {
            for (P point : mPoints) {
                if (mIcon != null) {
                    Point drawP = point.getDraw();
                    if(point.isMovable()) {
                        fromPixels(drawP.getX(), drawP.getY(), point.getLocation());
                    }else {
                        fromLocation(point.getLocation(), drawP);
                    }

                    canvas.drawBitmap(mIcon,
                            drawP.getX() - mIcon.getWidth() * 0.5f,
                            drawP.getY() - mIcon.getHeight() * 0.5f, null);
                    canvas.drawText(point.toString(), drawP.getX() - mIcon.getWidth(),
                            drawP.getY() + mIcon.getHeight(), mPaint);
                    drawOthers(canvas, point);

                }
            }
        }
    }

    public void setIcon(Bitmap icon) {
        mIcon = icon;
        mIconWidth = icon.getWidth() * 1.1f;
        mIconHeight = icon.getHeight() * 1.1f;
    }

    public void setPendingData(List<P> data) {
        mPoints.addAll(data);
        mMapView.refreshMap();
    }

    public void setCallback(OnOperationCallback callback) {
        this.mCallback = callback;
    }

    /**
     * 在图层中删除坐标点
     * @param point 要删除的点
     * @return 返回成功删除的点，否则返回null
     */
    public boolean remove(P point) {
        boolean ret;
        if(mCallback != null) {  // 在外部移除点
            ret = mCallback.onDeletePoint(point);
            if(!ret) {
                LogUtils.w("delete point(" + point.getLocation().getX() + "," + point.getLocation().getY() + ") failed outside!");
                return false;
            }
        }
        ret = mPoints.remove(point);

        if(ret) {
            // 刷新平面图
            getDirtyRect(point.getDraw(), mComputeRect);
            mMapView.refreshMapbydirty(mComputeRect);
            LogUtils.i((ret ? "succeed" : "fail") + " to remove " + point.toString());
        }

        return ret;
    }

    protected void drawOthers(Canvas canvas, P point) {

    }

    protected P findPoint(P point) {
        if(point != null) {
            int index = mPoints.indexOf(point);
            if(index != -1) {
                return mPoints.get(index);
            }
        }
        return null;
    }

    private class LayerGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(mCurrentPoint != null) {
                LogUtils.i("touch:click point " + mCurrentPoint.toString());
                if(mCallback != null) {
                    mCallback.onPressPoint(mCurrentPoint, new PointInfo(e.getRawX(), e.getRawY()));
                }
            } else {
                if(mCallback != null) {
                    mPointInfo.x = e.getX();
                    mPointInfo.y = e.getY();
                    P point = (P)mCallback.onCreatePoint(mPointInfo);
                    if(point != null) {
                        point.setLocation(fromPixels(e.getX(), e.getY(), null));
                        mPoints.add(point);
                        getDirtyRect(point.getDraw(), mComputeRect);
                        mMapView.refreshMapbydirty(mComputeRect);
                        mCurrentPoint = point;
                    }
                }
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if(mCurrentPoint != null) {
                LogUtils.d("point" + mCurrentPoint.toString() + " is long-pressed");
                if(mCallback != null) {
                    mCallback.onLongPressPoint(mCurrentPoint);
                }
//                mCurrentPoint = null;
            }
        }
    }

    @Nullable
    private Location fromPixels(float x, float y, Location l) {
        if (mMapView.getCenter() == null) {
            return null;
        }
        float mx = ((x - mMapView.getWidth() / 2) * (MapConstants.MAP_IC_M * mMapView.getScale() / MapConstants.MAP_DPI));
        float my = ((y - mMapView.getHeight() / 2) * (MapConstants.MAP_IC_M * mMapView.getScale() / MapConstants.MAP_DPI));
        float dx = (float) (mx * Math.cos(-mMapView.mapangle) - my * Math.sin(-mMapView.mapangle));
        float dy = (float) (mx * Math.sin(-mMapView.mapangle) + my * Math.cos(-mMapView.mapangle));

        float lx = mMapView.getCenter().getX() + dx;
        float ly = mMapView.getCenter().getY() + dy;
        if( l == null) {
            l = new Location(lx, ly);
        } else {
            l.a(lx);
            l.b(ly);
        }
        return l;
    }

    private Point fromLocation(Location l, Point p) {
        final Location center = mMapView.getCenter();
        if(center == null) {
            p.setX((float)(mMapSurface.getWidth() / 2));
            p.setY((float)(mMapSurface.getHeight() / 2));
        } else {
            float x = (l.getX() - center.getX()) / (MapConstants.MAP_IC_M * mMapView.getScale() / MapConstants.MAP_DPI);
            float y = (l.getY() - center.getY()) / (MapConstants.MAP_IC_M * mMapView.getScale() / MapConstants.MAP_DPI);
            if(mMapView.mapangle != 0.0F) {
                float realX = (float)((double)x * Math.cos((double)mMapView.mapangle) - (double)y * Math.sin((double)mMapView.mapangle));
                float realY = (float)((double)x * Math.sin((double)mMapView.mapangle) + (double)y * Math.cos((double)mMapView.mapangle));
                p.setX(realX + mMapView.getWidth() / 2);
                p.setY(realY + mMapView.getHeight() / 2);
            } else {
                p.setX(x + mMapView.getWidth() / 2);
                p.setY(y + mMapView.getHeight() / 2);
            }
        }
        return p;
    }

    private P findPointByTouch(float x, float y) {
        if(mPoints != null) {
            for(P point : mPoints) {
                Point drawP = point.getDraw();
                float left = drawP.getX() - mIconWidth;
                float right = drawP.getX() + mIconWidth;
                float top = drawP.getY() - mIconHeight;
                float bottom = drawP.getY() + mIconHeight;

                if(x >= left && x < right && y >= top && y < bottom) {
                    return point;
                }
            }
        }
        return null;
    }

    private Rect getDirtyRect(Point p, Rect r) {
        r.left = (int)(p.getX() - mIconWidth * 2f);
        r.right = (int)(p.getX() + mIconWidth * 2f);
        r.top = (int)(p.getY() - mIconHeight * 2f);
        r.right = (int)(p.getY() + mIconHeight * 2f);
        return r;
    }

}
