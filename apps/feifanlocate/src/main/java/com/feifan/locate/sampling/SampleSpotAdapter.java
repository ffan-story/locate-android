package com.feifan.locate.sampling;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.provider.LocateData.SampleSpot;
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;
import com.feifan.locate.widget.cursorwork.SimpleCursorAdapter;

/**
 * Created by xuchunlei on 16/8/31.
 */
public class SampleSpotAdapter extends RecyclerCursorAdapter<SampleSpotAdapter.ViewHolder> {

    @IdRes
    private static final int ID_DIRECTION = 1;
    @IdRes
    private static final int ID_COUNT = 2;
    @IdRes
    private static final int ID_STATUS = 3;
    @IdRes
    private static final int ID_CONTROL = 4;


    @Override
    protected void onBindViewHolder(ViewHolder holder, Cursor cursor){
        SampleSpotModel model = new SampleSpotModel(cursor);
        holder.bind(model);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = createView(parent.getContext());
        return new ViewHolder(v);
    }

    private View createView(Context context) {
        RelativeLayout row = new RelativeLayout(context);
        ViewGroup.LayoutParams rowParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        row.setLayoutParams(rowParams);
        row.setBackgroundColor(Color.WHITE);

        // 方向
        TextView directionV = new TextView(context);
        directionV.setId(ID_DIRECTION);
        directionV.setPadding(15, 15, 15, 15);
        directionV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        RelativeLayout.LayoutParams directionParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        directionParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        directionParams.addRule(RelativeLayout.CENTER_VERTICAL);
        row.addView(directionV, directionParams);

        // 数量
        TextView countV = new TextView(context);
        countV.setId(ID_COUNT);
        countV.setPadding(15, 15, 15, 15);
        countV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        RelativeLayout.LayoutParams countParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        countParams.setMargins(40, 0, 20, 0);
        countParams.addRule(RelativeLayout.RIGHT_OF, ID_DIRECTION);
        countParams.addRule(RelativeLayout.CENTER_VERTICAL);
        row.addView(countV, countParams);

        // 状态
        TextView statusV = new TextView(context);
        statusV.setId(ID_STATUS);
        statusV.setPadding(15, 15, 15, 15);
        statusV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        RelativeLayout.LayoutParams statusParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        statusParams.setMargins(40, 0, 20, 0);
        statusParams.addRule(RelativeLayout.RIGHT_OF, ID_COUNT);
        statusParams.addRule(RelativeLayout.CENTER_VERTICAL);
        row.addView(statusV, statusParams);

        // 控制
        TextView controlV = new TextView(context);
        controlV.setId(ID_CONTROL);
        controlV.setPadding(15, 15, 15, 15);
        controlV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        RelativeLayout.LayoutParams controlParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        controlParams.setMargins(0, 0, 20, 0);
        controlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        controlParams.addRule(RelativeLayout.CENTER_VERTICAL);
        row.addView(controlV, controlParams);

        return row;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(SampleSpotModel model) {
            bindText(ID_DIRECTION, String.format("%1$.4f", model.direction));
            bindText(ID_COUNT, String.valueOf(model.count));
            bindText(ID_STATUS, model.statusText);
            bindText(ID_CONTROL, getOperationText(model.status));
        }

        private void bindText(@IdRes int id, String text) {
            ((TextView)itemView.findViewById(id)).setText(text);
        }

        private String getOperationText(int status) {
            switch (status){
                case 1:
                    return "开始";
                case 2:
                    return "暂停";
                case 3:
                    return "继续";
                case 4:
                    return "导出";
                default:
                    return "";
            }
        }
    }

    public static class SampleSpotModel extends SimpleCursorAdapter.CursorModel {

        public float direction;
        public int count;
        public int status;
        public String statusText;

        public SampleSpotModel(Cursor cursor) {
            super(cursor);
            int idIndex = cursor.getColumnIndexOrThrow(SampleSpot._ID);
            id = cursor.getInt(idIndex);
            int directionIndex = cursor.getColumnIndexOrThrow(SampleSpot.D);
            direction = cursor.getFloat(directionIndex);
            int countIndex = cursor.getColumnIndexOrThrow(SampleSpot.COUNT);
            count = cursor.getInt(countIndex);
            int statusIndex = cursor.getColumnIndexOrThrow(SampleSpot.STATUS);
            status = cursor.getInt(statusIndex);
            statusText = getStatusText(status);

            LogUtils.d(toString());
        }

        private String getStatusText(int status) {
            switch (status) {
                case 1:
                    return "就绪";
                case 2:
                    return "运行";
                case 3:
                    return "暂停";
                case 4:
                    return "完成";
                default:
                    return "无效";
            }
        }

        @Override
        public String toString() {
            return String.format("%-10f%10d", direction, count);
        }
    }
}
