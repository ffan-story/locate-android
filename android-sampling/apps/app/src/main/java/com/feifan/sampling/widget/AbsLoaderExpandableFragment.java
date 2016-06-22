package com.feifan.sampling.widget;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CursorTreeAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuchunlei on 16/4/24.
 */
public abstract class AbsLoaderExpandableFragment extends AbsLoaderFragment {

    protected static final String TAG = AbsLoaderExpandableFragment.class.getSimpleName();


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader:create a loader with " + id);
        CursorLoader loader = null;
        if(id != getLoaderId()) {    // 子项Loader
            Uri childUri = getChildUri();
            loader = new CursorLoader(getContext(), childUri, null, null, null, null);
        }else {  // 父项Loader
            Uri groupUri = getContentUri();
            loader = new CursorLoader(getContext(), groupUri, null, null, null, null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        Log.d(TAG, "onLoadFinished:loader_id is " + id);
        if(id != getLoaderId()) {    // 加载子项数据
            onLoadChildren(id, data);
        }else {      // 加载父项数据
            onLoadGroup(data);
        }
    }

    protected abstract Uri getChildUri();
    protected abstract void onLoadGroup(Cursor data);
    protected abstract void onLoadChildren(int loaderId, Cursor data);

    /**
     * 使用Loader加载父项和子项数据的Adapter
     */
    protected abstract class LoaderCursorTreeAdapter extends CursorTreeAdapter {

        // 保存用于查询子项信息的父项位置与子项的LoaderID映射关系
        private final Map<Integer, Integer> mGroupMap;

        protected LayoutInflater mInflater;

        public LoaderCursorTreeAdapter(Cursor cursor, Context context) {
            super(cursor, context);
            mGroupMap = new HashMap<Integer, Integer>();
            mInflater = LayoutInflater.from(context);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            int groupPos = groupCursor.getPosition();
            // 为避免重复，groupId使用<LoaderID><_id>拼接而成，如11，12...，111，112
            int idIndex = groupCursor.getColumnIndexOrThrow(BaseColumns._ID);
            int groupId = Integer.valueOf(String.format("%d%d", getLoaderId(), groupCursor.getInt(idIndex)));
            Log.i(TAG, "children cursor:group's position is " + groupPos + ",group's id is " + groupId);

            mGroupMap.put(groupId, groupPos);
            getLoaderManager().initLoader(groupId, null, AbsLoaderExpandableFragment.this);

            return null;
        }

        public Map<Integer, Integer> getGroupMap() {
            return mGroupMap;
        }
    }
}
