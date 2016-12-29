package com.feifan.locate.widget.cursorwork;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.feifan.locate.utils.ScreenUtils;

import java.lang.reflect.Constructor;

/**
 * Grid风格CursorAdapter的简单实现版本
 *
 * Created by xuchunlei on 16/9/9.
 */
public class SimpleGridCursorAdapter<M extends CursorModel> extends RecyclerCursorAdapter<SimpleGridCursorAdapter.ViewHolder> {

    @IdRes
    private static final int ID_TITLE = 1;

    // model类型
    private Class<M> mClazz;

    public SimpleGridCursorAdapter(Class<M> clazz) {
        mClazz = clazz;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        try {
            Constructor<M> constructor = mClazz.getConstructor(Cursor.class);
            final M model = constructor.newInstance(cursor);
            holder.bind(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = createView(parent.getContext());
        return new ViewHolder(v);
    }

    private View createView(Context context) {

        FrameLayout layout = new FrameLayout(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ScreenUtils.getScreenHeight() / 4
        );
        layout.setLayoutParams(params);
        layout.setBackgroundColor(Color.CYAN);

        TextView titleV = new TextView(context);
        FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.gravity = Gravity.BOTTOM;
        titleV.setLayoutParams(titleParams);
        titleV.setId(ID_TITLE);
        titleV.setGravity(Gravity.CENTER);
        titleV.setTextColor(Color.WHITE);
        titleV.setPadding(5, 5, 5, 5);
        titleV.setBackgroundColor(Color.argb(0xa0, 0xa0, 0xa0, 0xa0));
        layout.addView(titleV);

        return layout;
    }

    public static class ViewHolder<M extends CursorModel> extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(M model) {
            itemView.setTag(model);
            TextView titleV = (TextView) itemView.findViewById(ID_TITLE);
            titleV.setText(model.toString());
        }
    }
}
