package com.feifan.locate.widget.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.feifan.locate.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 仿IOS风格的Popup菜单
 * Created by xuchunlei on 16/8/22.
 */
public class BubbleMenu {

    private Context mContext;
    private PopupWindow mWindow;
    private View.OnClickListener mListener;
    private boolean mClosable = true;

    private Collection<Item> mItems = new ArrayList<>();

    private static class Item {
        @IdRes
        int id = -1;
        String title;

        public Item(String title) {
            this(0, title);
        }

        public Item(@IdRes int id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    public BubbleMenu(Context context) {
        this(context, null, null);
    }

    public BubbleMenu(Context context, String[] items, View.OnClickListener listener) {
        mContext = context;
        if(items != null) {
            for(String title : items) {
                mItems.add(new Item(title));
            }
        }
        mListener = listener;
    }

    public void show(View v, int x, int y) {
        if(mWindow == null) {
            mWindow = createWindow(mContext, mItems);
        }
        mWindow.showAtLocation(v, Gravity.TOP | Gravity.LEFT, x, y);
    }

    public void dismiss() {
        if(mWindow != null) {
            mWindow.dismiss();
        }
    }

    public void addItem(@IdRes int id, String title) {
        mItems.add(new Item(id, title));
    }

    public void setOnItemListener(OnClickListener listener) {
        mListener = listener;
    }

    public void setClosableOutside(boolean closeable) {
        mClosable = closeable;
    }

    public boolean isShown() {
        return mWindow != null ? mWindow.isShowing() : false;
    }

    private PopupWindow createWindow(Context context, Collection<Item> items) {

        // 创建内容视图
        LinearLayout contentView = new LinearLayout(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int space = dp2px(context, 5);
        params.setMargins(space, space, space, space);
        contentView.setLayoutParams(params);
        int id = 0;
        for(Item item : items) {
            TextView itemView = new TextView(context);
            itemView.setText(item.title);
            if(item.id == -1) {
                itemView.setId(++id);
            }else {
                itemView.setId(item.id);
            }
            itemView.setTextColor(Color.WHITE);
            itemView.setPadding(space, space, space, space);
            contentView.addView(itemView);
            itemView.setOnClickListener(mListener);
        }

        // 创建window
        contentView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        PopupWindow window = new PopupWindow(contentView, contentView.getMeasuredWidth(), contentView.getMeasuredHeight(), false);
        // 点击外部区域，关闭window
        window.setBackgroundDrawable(new ColorDrawable(Color.argb(0x99,0x10,0x10,0x10)));
        window.setOutsideTouchable(mClosable);
        return window;
    }

    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
}
