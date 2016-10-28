package com.feifan.locate;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.feifan.locate.widget.ui.BaseActivity;

public class ToolbarActivity extends BaseActivity {

    public static final String EXTRA_KEY_FRAGMENT = "fragment";
    public static final String EXTRA_KEY_ARGUMENTS = "arguments";

    private IBackInterceptable mInterceptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);

        Toolbar toolbar = findView(R.id.toolbar_title);
        toolbar.setNavigationIcon(R.mipmap.ic_action_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInterceptor != null) {
                    if(!mInterceptor.isBackEnabled()) {
                        Toast.makeText(ToolbarActivity.this, "back is forbidden by some operation", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(mInterceptor.getResult() != null) {
                        setResult(RESULT_OK, mInterceptor.getResult());
                    }
                }
                finish();
            }
        });

        // 隐藏底部导航菜单
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        // 加载界面
        String fragmentName = getIntent().getStringExtra(EXTRA_KEY_FRAGMENT);
        Bundle fragmentArgs = getIntent().getBundleExtra(EXTRA_KEY_ARGUMENTS);
        Fragment fragment = Fragment.instantiate(this, fragmentName, fragmentArgs);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.toolbar_content, fragment)
                .commitAllowingStateLoss();
        if(fragment instanceof IBackInterceptable) {
            mInterceptor = (IBackInterceptable) fragment;
        }
    }
}
