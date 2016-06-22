package com.feifan.sampling.widget;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feifan.sampling.R;

import java.lang.reflect.Constructor;

/**
 * Created by xuchunlei on 16/5/6.
 */
public class SimpleAdapter<M extends SimpleAdapter.CursorModel> extends RecyclerCursorAdapter<SimpleAdapter.ViewHolder> {

    // model类型
    private Class<M> mClazz;
    public SimpleAdapter(Class<M> clz) {
        mClazz = clz;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_list_item, parent, false);
        return new ViewHolder(view);
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
     * Simple ViewHolder
     * @param <M>
     */
    public static class ViewHolder<M extends SimpleAdapter.CursorModel> extends RecyclerView.ViewHolder {

        private TextView mContentV;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentV = (TextView)itemView.findViewById(R.id.simple_item_content);
        }

        public void bind(M model) {
            mContentV.setText(model.toString());
            itemView.setTag(model);
        }
    }

    /**
     * 支持游标数据的model
     */
    public static class CursorModel {
        public int id;

        public CursorModel() {

        }

        public CursorModel(Cursor cursor) {

        }

    }
}
