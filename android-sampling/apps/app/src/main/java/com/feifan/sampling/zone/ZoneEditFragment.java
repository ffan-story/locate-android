package com.feifan.sampling.zone;


import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.feifan.sampling.http.ApiCreator;
import com.feifan.sampling.provider.SampleData;
import com.feifan.sampling.util.LogUtil;
import com.feifan.sampling.zone.model.SpotAddModel;
import com.feifan.sampling.zone.request.AddZoneInterface;
import com.libs.base.http.BpCallback;
import com.libs.base.model.BaseJsonBean;
import com.libs.ui.fragments.CommonFragment;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 *
 * 添加定位区域的fragment
 */
public class ZoneEditFragment extends CommonFragment {

    /** 活动返回码－定位区域 */
    public final static int RESPONSE_CODE_ZONE = 2;

    public ZoneEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 设置标题
        setTitle(R.string.zone_edit_title);
        final View view = inflater.inflate(R.layout.fragment_zone_edit, container, false);
        view.findViewById(R.id.zone_edit_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAdded()) {
                    // 更新返回参数
                    Intent data = new Intent();
                    final EditText nameEdt = (EditText)view.findViewById(R.id.zone_edit_name_edt);

                    if(!TextUtils.isEmpty(nameEdt.getText())) {
                        startNetRequest(nameEdt.getText().toString());
                        data.putExtra(Constants.EXTRA.KEY_NAME, nameEdt.getText().toString());
                    }

                    // 返回结果
                    final FragmentActivity activity = getActivity();
                    activity.setResult(RESPONSE_CODE_ZONE, data);
                    activity.finish();
                }
            }
        });
        return view;
    }

    private void startNetRequest(final String name){
        AddZoneInterface request = ApiCreator.getInstance().createApi(AddZoneInterface.class);
        Call<BaseJsonBean<SpotAddModel>> call = request.addZone(name);
        call.enqueue(new BpCallback<BaseJsonBean<SpotAddModel>>() {
            @Override
            public void onResponse(BaseJsonBean<SpotAddModel> helpCenterModel) {
                String id = helpCenterModel.getData().getId();
                System.out.println(id);
                saveRemoteId(id,name);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void saveRemoteId(String id,String name){
        if(!TextUtils.isEmpty(id)){
            ZoneModel zone = new ZoneModel(name,id);
            // 保存到数据库
            ContentValues values = new ContentValues();
            zone.fill(values);
            Uri spotUri = getActivity().getContentResolver().insert(SampleData.Zone.CONTENT_URI, values);
            zone.id = Integer.valueOf(spotUri.getLastPathSegment());
            LogUtil.i(Constants.DEBUG_TAG, zone.toString() + "'id is " + zone.id);
        }
    }
}
