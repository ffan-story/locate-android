package com.feifan.locate.sampling;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.provider.LocateData;
import com.feifan.locate.provider.LocateData.SampleColumns;
import com.feifan.locate.sampling.model.SampleModel;
import com.feifan.locate.sampling.model.SampleSpotModel;
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;

import java.lang.reflect.Constructor;

/**
 * Created by xuchunlei on 16/10/13.
 */

public class SampleAdapter<M extends SampleModel> extends RecyclerCursorAdapter<SampleAdapter.SampleViewHolder<M>> {

    @IdRes
    private static final int ID_NAME = 1;
    @IdRes
    private static final int ID_TOTAL = 3;
    @IdRes
    private static final int ID_PROGRESS = 4;
    @IdRes
    private static final int ID_CONTROL = 5;

    private Class<M> mClazz;
    private OnClickListener mListener;

    public SampleAdapter(Class<M> clazz, final OnSampleClickListener listener) {
        mClazz = clazz;
        mListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case ID_CONTROL:
                        M model = (M)((View)v.getParent()).getTag();
                        listener.onOperationClick(model);
                        break;
                }
            }
        };
    }

    @Override
    protected void onBindViewHolder(SampleViewHolder<M> holder, Cursor cursor) {
        try {
            Constructor<M> constructor = mClazz.getConstructor(Cursor.class);
            final M model = constructor.newInstance(cursor);
            holder.bind(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SampleViewHolder<M> onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = createView(parent.getContext());
        return new SampleViewHolder(v);
    }

    private View createView(Context context) {
        RelativeLayout row = new RelativeLayout(context);
        ViewGroup.LayoutParams rowParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        row.setLayoutParams(rowParams);
        row.setBackgroundColor(Color.WHITE);

        // 样本名称
        TextView nameV = new TextView(context);
        nameV.setId(ID_NAME);
        nameV.setPadding(15, 15, 15, 15);
        nameV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        nameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        nameParams.addRule(RelativeLayout.CENTER_VERTICAL);
        row.addView(nameV, nameParams);

        // 样本数量
        TextView countV = new TextView(context);
        countV.setId(ID_TOTAL);
        countV.setPadding(15, 15, 15, 15);
        countV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        RelativeLayout.LayoutParams countParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        countParams.setMargins(40, 0, 20, 0);
        countParams.addRule(RelativeLayout.RIGHT_OF, ID_NAME);
        countParams.addRule(RelativeLayout.CENTER_VERTICAL);
        row.addView(countV, countParams);

        // 采集进度
        TextView progressV = new TextView(context);
        progressV.setId(ID_PROGRESS);
        progressV.setPadding(15, 15, 15, 15);
        progressV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        RelativeLayout.LayoutParams statusParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        statusParams.setMargins(40, 0, 20, 0);
        statusParams.addRule(RelativeLayout.RIGHT_OF, ID_TOTAL);
        statusParams.addRule(RelativeLayout.LEFT_OF, ID_CONTROL);
        statusParams.addRule(RelativeLayout.CENTER_VERTICAL);
        row.addView(progressV, statusParams);

        // 操作
        TextView controlV = new TextView(context);
        controlV.setId(ID_CONTROL);
        controlV.setPadding(15, 15, 15, 15);
        controlV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        controlV.setOnClickListener(mListener);
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

    public static class SampleViewHolder<M extends SampleModel> extends RecyclerView.ViewHolder {

        public SampleViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(M model) {
            itemView.setTag(model);
            bindText(ID_NAME, model.toString());
            bindText(ID_TOTAL, String.valueOf(model.total));
            bindText(ID_PROGRESS, model.progress);
            bindText(ID_CONTROL, getOperationText(model.status));
        }

        private void bindText(@IdRes int id, String text) {
            TextView t = (TextView)itemView.findViewById(id);
            t.setText(text);
        }

        // FIXME 可以设计成protected方法,通过重载个性化定制
        private String getOperationText(int status) {
            switch (status){
                case SampleColumns.STATUS_READY:
                    return "启动";
                case SampleColumns.STATUS_RUNNING:
                    return "停止";
                case SampleColumns.STATUS_FINISH:
                    return "保存";
                default:
                    return "";
            }
        }
    }

    public interface OnSampleClickListener<M extends SampleModel>{
        /**
         * 点击操作按钮
         * @param model
         */
        void onOperationClick(M model);
    }
}
