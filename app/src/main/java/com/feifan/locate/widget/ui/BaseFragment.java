package com.feifan.locate.widget.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.IBackInterceptable;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment基类
 * <p>
 *     封装了Toolbar右侧按钮功能和左侧返回按钮功能，一般与ToolbarActivity一起使用
 * </p>
 *
 *
 * Created by xuchunlei on 16/4/25.
 */
public abstract class BaseFragment extends Fragment implements MenuItem.OnMenuItemClickListener, IBackInterceptable {

    public static final int NO_RES = -1;
    private static final String NO_STRING = "n/a";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int titleRes = getTitleResource();
        if(titleRes != NO_RES) {
            getActivity().setTitle(titleRes);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        LogUtils.d("onCreateOptionsMenu is called");
        List<MenuInfo> infoList = getMenuList();

        if(infoList != null) {
            int actionType =
                    infoList.size() == 1 ? MenuItem.SHOW_AS_ACTION_IF_ROOM : MenuItem.SHOW_AS_ACTION_NEVER;
            for(MenuInfo info : infoList) {
                MenuItem item;
                if(info.titleRes != NO_RES) {
                    item = menu.add(info.groupId, info.id, 1, info.titleRes);
                }else {
                    item = menu.add(info.groupId, info.id, 1, NO_STRING);
                }
                if(info.iconRes != NO_RES) {
                    item.setIcon(info.iconRes);
                }
                item.setShowAsAction(actionType);
                item.setOnMenuItemClickListener(this);
            }
        }
    }

    /**
     * 获取标题栏右侧菜单列表
     * <p>
     *     子类通过以下方法更新菜单
     *     ArrayList<MenuInfo> menuList = super.getMenuList();
     *     menuList.clear();
     *     menuList.add(new MenuInfo(id, title ,icon));
     *     ...
     *     return menuList;
     * </p>
     * @return
     */
    protected List<MenuInfo> getMenuList() {

        return new ArrayList<MenuInfo>();
    }

    @Override
    public Intent getResult() {
        return null;
    }

    @Override
    public boolean isBackEnabled() {
        return true;
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

    /**
     * 获取标题资源
     * <p>
     *     界面的标题固定时，通过该方法提供
     * </p>
     * @return
     */
    protected @StringRes int getTitleResource(){
        return NO_RES;
    }

    /**
     * 设置标题
     * <p>
     *     界面的标题动态变化时，通过该方法设置
     * </p>
     * @param title
     */
    protected void setTitle(String title) {
        if(isAdded() && !TextUtils.isEmpty(title)) {
            getActivity().setTitle(title);
        }
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
        public int groupId;

        public MenuInfo(@IdRes int id, @DrawableRes int iconRes, @StringRes int titleRes) {
            this.id = id;
            this.iconRes = iconRes;
            this.titleRes = titleRes;
            this.groupId = Menu.NONE;
        }

        public MenuInfo(@IdRes int id, @DrawableRes int iconRes) {
            this(id, iconRes, NO_RES);
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }
    }
}
