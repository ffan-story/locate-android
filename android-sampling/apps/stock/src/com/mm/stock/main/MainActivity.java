package com.mm.stock.main;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.libs.ui.activities.BaseMultiActivity;
import com.libs.ui.fragments.FragmentDelegate;
import com.libs.ui.fragments.FragmentParams;
import com.mm.stock.R;
import com.mm.stock.charts.PreviewLineChartFragment;

import java.util.ArrayList;

public class MainActivity extends BaseMultiActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                }
                menuItem.setChecked(true);//点击了把它设为选中状态
                mDrawerLayout.closeDrawers();//关闭抽屉
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public int getContentView() {
        return R.layout.biz_main_activity;
    }

    @Override
    protected FragmentDelegate getFragmetnDelegate() {
        return mFragmentDelegate = new FragmentDelegate(this, buildFragmentParamList(), new Bundle());
    }

    private ArrayList<FragmentParams> buildFragmentParamList() {
        ArrayList<FragmentParams> list = new ArrayList<FragmentParams>();
        list.add(getMainFragmentParams());
        return list;
    }

    private FragmentParams getMainFragmentParams() {
        return new FragmentParams(R.layout.activity_main, R.id.main_content, "PreviewLineChartFragment", PreviewLineChartFragment.class.getName(), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}
