package com.feifan.locate.widget.cursorwork;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Constructor;

/**
 * SimpleCursorAdapter
 * <pre>
 *     用于RecyclerView的CursorAdapter，简单实现使用CardView显示文本
 * </pre>
 *
 * Created by xuchunlei on 16/5/6.
 */
public class SimpleCursorAdapter<M extends CursorModel> extends RecyclerCursorAdapter<SimpleCursorAdapter.ViewHolder> {

    private static final String SIMPLE_TEXT_TAG = "SIMPLE_TEXT";

    // model类型
    private Class<M> mClazz;

    public SimpleCursorAdapter(Class<M> clz) {
        mClazz = clz;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = createView(parent.getContext());
        return new ViewHolder(v);
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

    /**
     * 创建显示内容的视图，可以重载此方法自定义视图
     * @param context
     * @return
     */
    protected View createView(Context context) {
        CardView card = new CardView(context);

        // 创建CardView，设置尺寸参数
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(params);
        card.setContentPadding(15, 15, 15, 15);

        // 添加TextView，设置尺寸参数
        TextView tv = new TextView(context);
        tv.setLayoutParams(params);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setTextColor(Color.BLACK);
        tv.setTag(SIMPLE_TEXT_TAG);

        // 将TextView加入到CardView
        card.addView(tv);


        return card;
    }

    /**
     * Simple ViewHolder
     * @param <M>
     */
    public static class ViewHolder<M extends CursorModel> extends RecyclerView.ViewHolder {

        private TextView mContentV;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentV = (TextView)itemView.findViewWithTag(SIMPLE_TEXT_TAG);
        }

        public void bind(M model) {
            mContentV.setText(model.toString());
            itemView.setTag(model);
        }
    }
}
