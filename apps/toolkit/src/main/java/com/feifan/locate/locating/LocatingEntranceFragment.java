package com.feifan.locate.locating;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.feifan.locate.R;
import com.feifan.locate.baseuilib.ToolbarActivity;
import com.feifan.locate.common.BuildingModel;
import com.feifan.locate.provider.LocateData.Zone;
import com.feifan.locate.widget.BaseFragment;
import com.feifan.locatelib.LocatorFactory;

/**
 * Created by xuchunlei on 2016/11/10.
 */

public class LocatingEntranceFragment extends BaseFragment implements View.OnClickListener {

    private BuildingModel mBuildingModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_locating_entrance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView onlineV = findView(R.id.entrance_online);
        onlineV.setOnClickListener(this);
        final TextView offlineV = findView(R.id.entrance_offline);
        offlineV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.entrance_offline:
                enterLocating(LocatorFactory.LOCATING_MODE_OFFLINE);
                break;
            case R.id.entrance_online:
                Toast.makeText(getContext(), "not supported now", Toast.LENGTH_SHORT).show();
//                enterLocating(LocatorFactory.LOCATING_MODE_ONLINE);
                break;
        }
    }

    private void enterLocating(int mode) {
        Intent intent = new Intent(getContext().getApplicationContext(), ToolbarActivity.class);
        intent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, RealtimePlanFragment.class.getName());

        Bundle args = new Bundle();
        args.putString(RealtimePlanFragment.LOADER_KEY_SELECTION, "building=?");
        args.putStringArray(RealtimePlanFragment.LOADER_KEY_SELECTION_ARGS,
                new String[]{ String.valueOf(-1) });
        args.putString(RealtimePlanFragment.LOADER_KEY_ORDER_BY, Zone.FLOOR_NO + " DESC");
        args.putInt(RealtimePlanFragment.EXTRA_KEY_MODE, mode);
        intent.putExtra(ToolbarActivity.EXTRA_KEY_ARGUMENTS, args);

        startActivity(intent);
    }
}
