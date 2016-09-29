package com.feifan.locate.widget.cursorwork;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.widget.ui.BaseFragment;

/**
 * 支持LoaderManager的Fragment
 *
 * Created by xuchunlei on 16/4/24.
 */
public abstract class AbsLoaderFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "AbsLoaderFragment";

    /** 参数名－Loader参数 */
    public static final String EXTRA_KEY_LOADER_ARGS = "args";

    /** 创建Loader时的参数－selection */
    protected static final String LOADER_KEY_SELECTION = "selection";
    /** 创建Loader时的参数－selectionArgs */
    protected static final String LOADER_KEY_SELECTION_ARGS = "selectionArgs";
    /** 创建Loader时的参数-orderBy */
    protected static final String LOADER_KEY_ORDER_BY = "orderBy";

    /** 生成Loader的ID */
    protected abstract int getLoaderId();
    /** 获得ContentProvider的Uri */
    protected abstract Uri getContentUri();
    /** 获得Adapter */
    protected abstract <A extends ICursorAdapter> A getAdapter();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getLoaderId() != 0) {
            getLoaderManager().initLoader(getLoaderId(), getArguments(), this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = args != null ? args.getString(LOADER_KEY_SELECTION) : null;
        String[] selectionArgs = args != null ? args.getStringArray(LOADER_KEY_SELECTION_ARGS) : null;
        String orderBy = args != null ? args.getString(LOADER_KEY_ORDER_BY) : null;
        LogUtils.d("selection = " + selection + ",selectionArgs = " + args2string(selectionArgs));
        return new CursorLoader(getContext(), getContentUri(), null, selection, selectionArgs, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished:we got " + data.getCount() + " data item from via loader " + loader.getId());
        if(getAdapter() != null) {
            getAdapter().swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(getAdapter() != null) {
            getAdapter().swapCursor(null);
        }
    }

    private String args2string(String[] args) {
        String result = "";
        if(args != null && args.length != 0) {
            for(String arg : args) {
                result += arg + ",";
            }
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
