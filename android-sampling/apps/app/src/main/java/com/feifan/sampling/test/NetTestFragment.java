package com.feifan.sampling.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.sampling.R;
import com.feifan.sampling.http.ApiCreator;
import com.feifan.sampling.test.model.LocModel;
import com.feifan.sampling.test.request.TestRequestInterface;
import com.libs.base.http.BpCallback;
import com.libs.base.model.BaseJsonBean;

import retrofit2.Call;

/**
 * Created by mengmeng on 16/5/17.
 */
public class NetTestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_net,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null){
            view.findViewById(R.id.spot_edit_save_btn).setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    TestRequestInterface request = ApiCreator.getInstance().createApi(TestRequestInterface.class);
                    Call<BaseJsonBean<LocModel>> call = request.getZoneList(0,10);
                    call.enqueue(new BpCallback<BaseJsonBean<LocModel>>() {
                        @Override
                        public void onResponse(BaseJsonBean<LocModel> helpCenterModel) {
                            System.out.println(helpCenterModel.getData());
                        }

                        @Override
                        public void onFailure(String message) {

                        }
                    });
                }
            });
        }
    }
}
