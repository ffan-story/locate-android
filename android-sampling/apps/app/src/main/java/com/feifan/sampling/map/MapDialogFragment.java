package com.feifan.sampling.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.feifan.sampling.scan.ScanFragment;
import com.libs.ui.activities.BaseActivity;
import com.libs.ui.fragments.BaseDialogFragment;
import com.libs.ui.fragments.FragmentDelegate;

/**
 * Created by mengmeng on 16/7/4.
 */
public class MapDialogFragment extends BaseDialogFragment {
    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.biz_map_fragment_dialog_layout,container,false);
        Bundle argus = getArguments();
        if(argus != null){
            final float x = argus.getFloat("param_x");
            final float y = argus.getFloat("param_y");
            String title = "( "+x+" , "+y+" )";
            ((TextView)view.findViewById(R.id.title)).setText(title);
            view.findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putFloat(Constants.EXTRA_KEY_SPOT_X,x);
                    bundle.putFloat(Constants.EXTRA_KEY_SPOT_Y,y);
                    Intent intent = FragmentDelegate.getIntent(getContext(),0,0,"ScanFragment", ScanFragment.class.getName(), BaseActivity.class,bundle);
                    startActivity(intent);
                }
            });
        }
        return view;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int[] getDialogSize() {
        return new int[] { WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT };
    }

    @Override
    public float getDimAmount() {
        return 0.3f;
    }
}
