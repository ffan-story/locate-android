package com.feifan.sampling.spot;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.feifan.sampling.base.log.GlobalState;
import com.feifan.sampling.http.ApiCreator;
import com.feifan.sampling.spot.request.AddSpotInterface;
import com.feifan.sampling.zone.model.SpotAddModel;
import com.libs.base.http.BpCallback;
import com.libs.base.model.BaseJsonBean;
import com.libs.ui.fragments.CommonFragment;

import retrofit2.Call;


/**
 * 编辑采集点Fragment
 */
public class SpotEditFragment extends CommonFragment {

    /** 活动返回码－采集点 */
    public final static int RESPONSE_CODE_SPOT = 1;

    /** 参数名－x轴坐标 */
    public final static String EXTRA_NAME_X = "x";
    /** 参数名－y轴坐标 */
    public final static String EXTRA_NAME_Y = "y";
    /** 参数名－方向(角度)坐标 */
    public final static String EXTRA_NAME_D = "d";

    private String mZoneId;

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_spot_edit, container, false);
        mZoneId = getArguments().getString("zoneid");
        view.findViewById(R.id.spot_edit_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAdded()) {
                    // 更新返回参数
                    Intent data = new Intent();
                    final EditText xEdt = (EditText)view.findViewById(R.id.spot_edit_x_edt);
                    final EditText yEdt = (EditText)view.findViewById(R.id.spot_edit_y_edt);
                    final EditText dEdt = (EditText)view.findViewById(R.id.spot_edit_d_edt);
                    if(!TextUtils.isEmpty(xEdt.getText())) {
                        data.putExtra(EXTRA_NAME_X, Float.valueOf(xEdt.getText().toString()));
                    }
                    if(!TextUtils.isEmpty(yEdt.getText())) {
                        data.putExtra(EXTRA_NAME_Y, Float.valueOf(yEdt.getText().toString()));
                    }
                    if(!TextUtils.isEmpty(dEdt.getText())) {
                        data.putExtra(EXTRA_NAME_D, Float.valueOf(dEdt.getText().toString()));
                    }
                    if(GlobalState.isOnLineState()) {
                        startNetRequest(xEdt.getText().toString(), yEdt.getText().toString(), dEdt.getText().toString(), mZoneId);
                    }else {
                        SpotHelper.saveRemoteId(getActivity(),xEdt.getText().toString(), yEdt.getText().toString(), dEdt.getText().toString(), mZoneId, String.valueOf(Constants.DEFAULT_REMOTE_ID));
                        onBackPressed();
                    }
                }
            }
        });
        return view;
    }

    private void startNetRequest(final String x,final String y,final String d,final String zoneid){
        AddSpotInterface request = ApiCreator.getInstance().createApi(AddSpotInterface.class);
        Call<BaseJsonBean<SpotAddModel>> call = request.addSpot(x,y,d,zoneid);
        call.enqueue(new BpCallback<BaseJsonBean<SpotAddModel>>() {
            @Override
            public void onResponse(BaseJsonBean<SpotAddModel> helpCenterModel) {
                String remoteid = helpCenterModel.getData().getId();
                System.out.println(remoteid);
                SpotHelper.saveRemoteId(getActivity(),x,y,d,zoneid,remoteid);
                onBackPressed();
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

}
