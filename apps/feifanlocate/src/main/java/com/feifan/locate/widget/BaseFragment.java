package com.feifan.locate.widget;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装了TitleBar右侧按钮功能
 * Created by xuchunlei on 16/4/25.
 */
public abstract class BaseFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    private static final int NO_INTEGER = -1;
    private static final String NO_STRING = "n/a";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

//        inflater.inflate(R.menu.top_menu, menu);
        List<MenuInfo> infoList = getMenuList();
        if(infoList != null) {
            for(MenuInfo info : infoList) {
                MenuItem item;
                if(info.titleRes != NO_INTEGER) {
                    item = menu.add(Menu.NONE, info.id, 1, info.titleRes);
                }else {
                    item = menu.add(Menu.NONE, info.id, 1, NO_STRING);
                }
                if(info.iconRes != NO_INTEGER) {
                    item.setIcon(info.iconRes);
                }
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                item.setOnMenuItemClickListener(this);
            }
        }
    }

    protected List<MenuInfo> getMenuList() {

        return new ArrayList<MenuInfo>();
    }

    /**
     * 查找到指定ID的视图
     * @param id
     * @param <T>
     * @return
     */
    protected <T> T findView(@IdRes int id) {
        return (T)getView().findViewById(id);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    /**
     * 菜单信息类
     *
     * <pre>
     *     封装了创建菜单所需要的信息
     * </pre>
     *
     */
    public static class MenuInfo {
        public int id;
        public int iconRes;
        public int titleRes;

        public MenuInfo(int id, int iconRes, int titleRes) {
            this.id = id;
            this.iconRes = iconRes;
            this.titleRes = titleRes;
        }

        public MenuInfo(int id, int iconRes) {
            this.id = id;
            this.iconRes = iconRes;
            this.titleRes = NO_INTEGER;
        }
    }
}
