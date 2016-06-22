package com.feifan.sampling.main;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.feifan.sampling.R;
import com.feifan.sampling.SampleApplication;
import com.feifan.sampling.http.ApiCreator;
import com.feifan.sampling.main.model.IsConnectedModel;
import com.feifan.sampling.main.request.IsConnetedInterface;
import com.feifan.sampling.scan.ScanFragment;
import com.feifan.sampling.set.SettingFragment;
import com.feifan.sampling.uuid.UUidFragment;
import com.feifan.sampling.zone.ZoneListFragment;
import com.libs.base.http.BpCallback;
import com.libs.base.model.BaseJsonBean;
import com.libs.ui.activities.BaseMultiActivity;
import com.libs.ui.fragments.FragmentDelegate;
import com.libs.ui.fragments.FragmentParams;
import com.libs.utils.SystemUtils;

import java.util.ArrayList;

import retrofit2.Call;

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
                    case R.id.nav_uuid:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content,new UUidFragment()).commit();
                        mFragmentDelegate.replaceFragment("UUidFragment", R.id.main_content, UUidFragment.class.getName(), null);
//                        mFragmentDelegate.replaceFragment("TieListFragment", R.id.content, TieListFragment.class.getName(), null);
                        break;
                    case R.id.nav_scan:
                        mFragmentDelegate.replaceFragment("ScanFragment", R.id.main_content, ScanFragment.class.getName(), null);
                        break;
                    case R.id.nav_gallery:
//						getSupportFragmentManager().beginTransaction().replace(R.id.content,new FragmentThree()).commit();
                        break;
                    case R.id.nav_slideshow:
//						getSupportFragmentManager().beginTransaction().replace(R.id.content,new FragmentThree()).commit();
//						mToolbar.setTitle("附近的人");
//                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new SendMicroBlogFragment()).commit();
                        break;
                    case R.id.nav_setting:
//						getSupportFragmentManager().beginTransaction().replace(R.id.content,new FragmentThree()).commit();
//						mToolbar.setTitle("附近的人");
                        mFragmentDelegate.replaceFragment("SettingFragment", R.id.main_content, SettingFragment.class.getName(), null);
                        break;
                }
                menuItem.setChecked(true);//点击了把它设为选中状态
                mDrawerLayout.closeDrawers();//关闭抽屉
                return true;
            }
        });
        startNetRequest(SystemUtils.getBrand());
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
        return new FragmentParams(R.layout.activity_main, R.id.main_content, "ZoneListFragment", ZoneListFragment.class.getName(), null);
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


    private void startNetRequest(String name){
        IsConnetedInterface request = ApiCreator.getInstance().createApi(IsConnetedInterface.class);
        Call<BaseJsonBean<IsConnectedModel>> call = request.isConnected(name);
        call.enqueue(new BpCallback<BaseJsonBean<IsConnectedModel>>() {
            @Override
            public void onResponse(BaseJsonBean<IsConnectedModel> model) {
                SampleApplication app = (SampleApplication) getApplication();
                app.setServiceOn(true);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }
}
