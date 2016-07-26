package com.feifan.sampling.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuchunlei on 16/4/25.
 */
public abstract class BaseFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.top_menu, menu);
        List<MenuInfo> infoList = getMenuList();
        if(infoList != null) {
            for(MenuInfo info : infoList) {
                MenuItem item = menu.add(Menu.NONE, info.id, 1, info.titleRes);
                if(info.iconRes != Constants.NO_INTEGER) {
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
            this.titleRes = R.string.no_title_text;
        }
    }
}
