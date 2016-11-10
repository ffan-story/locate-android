package com.feifan.locate.locating;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.R;
import com.feifan.locate.widget.ui.BaseFragment;
import com.feifan.locatelib.PlazaDetectorService;
import com.feifan.locatelib.cache.PlazaBeaconInfo.PlazaInfo;

/**
 * Created by xuchunlei on 2016/11/10.
 */

public class LocatingEntranceFragment extends BaseFragment implements View.OnClickListener {

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

        // 获取PlazaId
        ResultReceiver plazaReceiver = new ResultReceiver(new Handler()){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                switch (resultCode) {
                    case PlazaDetectorService.RESULT_CODE_PLAZA:
                        PlazaInfo info = resultData.getParcelable(PlazaDetectorService.RESULT_KEY_PLAZA);
                        onlineV.setText(getString(R.string.locating_title_format,
                                onlineV.getText(), info.plazaName));
                        offlineV.setText(getString(R.string.locating_title_format,
                                offlineV.getText(), info.plazaName));
                        break;
                    default:
                        break;
                }
            }
        };
        Intent intent = new Intent(getContext(), PlazaDetectorService.class);
        intent.putExtra("receiver", plazaReceiver);
        getContext().startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.entrance_offline:
                break;
            case R.id.entrance_online:
                break;
        }
    }
}
