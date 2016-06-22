package com.feifan.sampling.widget;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;

/**
 * 可用于RecyclerView的游标数据适配器
 *
 * Created by xuchunlei on 16/4/21.
 */
public abstract class RecyclerCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    // 数据游标
    private Cursor mCursor;
    // id列索引
    private int mIdIndex;

    public RecyclerCursorAdapter() {

    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(holder, mCursor);
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        return mCursor.getLong(mIdIndex);
    }

    public Cursor swapCursor(Cursor newCursor) {
        if(newCursor == mCursor) {
            return null;
        }

        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        mIdIndex = mCursor != null ? mCursor.getColumnIndex(BaseColumns._ID) : -1;
        notifyDataSetChanged();

        return oldCursor;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * 使用游标绑定ViewHolder
     * @param holder
     * @param cursor
     */
    protected abstract void onBindViewHolder(VH holder, Cursor cursor);
}
