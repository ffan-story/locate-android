package com.feifan.locate.widget.cursorwork;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.baselib.utils.LogUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 可用于ViewPager的游标数据适配器
 *
 * Created by xuchunlei on 16/9/20.
 */
public abstract class CursorPagerAdapter<T extends View> extends PagerAdapter implements ICursorAdapter {

    protected Context mContext;
    private Cursor mCursor;
    private Class<T> mClazz;

    // view
    private LruCache<Integer, T> mItems;

//    private int mIdIndex;
    private int mTitleIndex;

    public CursorPagerAdapter(Context context, Class<T> clazz) {
        this.mContext = context;
        this.mClazz = clazz;

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        mItems = new LruCache<>(maxMemory / 8);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        return mCursor.getString(mTitleIndex);
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view   Page View to check for association with <code>object</code>
     * @param object Object to check for association with <code>view</code>
     * @return true if <code>view</code> is associated with the key object <code>object</code>
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        T item = null;

        // 在创建集合中查找
        if (mItems.size() > position) {
            item = mItems.get(position);
            if (item != null) {
                LogUtils.d("position " + position + " instance found, just got it");
                return item;
            }
        }
        item = getItem(position);
        if(item != null) {
            container.addView(item);
            LogUtils.d("position " + position + " instance created");
        }

        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((T)object);
        if(mItems.remove(position) != null) {
            LogUtils.d("destroy " + position + " item");
        }else {
            LogUtils.w("destroy " + position + " item failed");
        }

    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        if(newCursor == mCursor) {
            return null;
        }

        Cursor oldCursor = mCursor;
        mCursor = newCursor;
//        mIdIndex = (mCursor != null ? mCursor.getColumnIndex(BaseColumns._ID) : -1);
        mTitleIndex = (mCursor != null ? mCursor.getColumnIndex(getTitleColumnName()) : -1);
        notifyDataSetChanged();

        return oldCursor;
    }

    /**
     * 获得标题列名，用于获取标题
     * <p>
     *     默认为title，重载此方法可修改标题列名s
     * </p>
     * @return
     */
    protected String getTitleColumnName() {
        return "title";
    }

    protected abstract void renderView(T view, Cursor cursor, int position);

    private T getItem(int position){
        if (mCursor == null)
            return null;
        mCursor.moveToPosition(position);
        T item;
        try {
            Constructor<T> constructor = mClazz.getConstructor(Context.class);
            item = constructor.newInstance(mContext);
            renderView(item, mCursor, position);
            mItems.put(position, item);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        LogUtils.d("position " + position + " is created");
        return item;
    }

}
