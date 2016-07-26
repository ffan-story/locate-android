package com.feifan.sampling.widget.ExpandableAdapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

import com.feifan.sampling.widget.RecyclerCursorAdapter;

/**
 * 可展开子项的数据适配器，用于RecyclerView
 * Created by xuchunlei on 16/4/23.
 */
public abstract class ExpandableRecyclerCursorAdapter<PVH extends ParentViewHolder, CVH extends ChildViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 节点类型－父项
    private static final int TYPE_PARENT = 0;
    // 节点类型－子项
    private static final int TYPE_CHILD = 1;

    private CursorHelper mParentHelper;

    public ExpandableRecyclerCursorAdapter(Cursor cursor) {
        mParentHelper = new CursorHelper(cursor);
    }

    /**
     * 管理Cursor的帮助类
     */
    class CursorHelper {
        private Cursor mCursor;
        public CursorHelper(Cursor cursor) {
            this.mCursor = cursor;
        }
    }
}
