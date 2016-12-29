package com.feifan.locate.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianying on 2016/12/19.
 */

public class SimpleListAdapter<M extends SimpleListAdapter.ListModel> extends RecyclerView.Adapter<SimpleListAdapter.ViewHolder> {

    private static final String SIMPLE_TEXT_TAG = "SIMPLE_TEXT";

    // model类型
    private Class<M> mClazz;

    // data
    private List<M> mData = new ArrayList<>();

    public SimpleListAdapter(Class<M> clazz) {
        mClazz = clazz;
    }

    public void setData(List<M> data) {
        mData.clear();
        mData.addAll(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = createView(parent.getContext());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final M model = mData.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private View createView(Context context) {

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
    public static class ViewHolder<M extends ListModel> extends RecyclerView.ViewHolder {

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

    public static class ListModel {

    }
}
