package com.feifan.sampling.map;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.feifan.sampling.R;
import com.libs.ui.fragments.BaseDialogFragment;

/**
 * Created by mengmeng on 16/7/4.
 */
public class MapDialogFragment extends BaseDialogFragment {
    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.biz_map_fragment_dialog_layout,container,false);
        Bundle argus = getArguments();
        if(argus != null){
            float x = argus.getFloat("param_x");
            float y = argus.getFloat("param_y");
            String title = "( "+x+" , "+y+" )";
            ((TextView)view.findViewById(R.id.title)).setText(title);
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
