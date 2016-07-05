package com.feifan.sampling.zone;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.feifan.sampling.SpotListActivity;
import com.feifan.sampling.http.ApiCreator;
import com.feifan.sampling.provider.ProviderHelper;
import com.feifan.sampling.provider.SampleData.Zone;
import com.feifan.sampling.spot.SpotListFragment;
import com.feifan.sampling.test.model.LocItem;
import com.feifan.sampling.test.model.LocModel;
import com.feifan.sampling.util.LogUtil;
import com.feifan.sampling.widget.SimpleAdapter;
import com.feifan.sampling.widget.SpaceItemDecoration;
import com.feifan.sampling.zone.request.ZoneListRequest;
import com.libs.base.http.BpCallback;
import com.libs.base.model.BaseJsonBean;
import com.libs.ui.activities.BaseActivity;
import com.libs.ui.fragments.CommonFragment;
import com.libs.ui.fragments.FragmentDelegate;

import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZoneListFragment extends CommonFragment<Cursor> implements View.OnClickListener {

    private SimpleAdapter<ZoneModel> mAdapter;

    // 启动采集点活动
    private Intent mIntent;
    private int START_PAGE_INDEX = 1;
    private int PAGE_LENGTH = 20;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIntent = new Intent(context.getApplicationContext(), SpotListActivity.class);
    }

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.biz_zone_list, container, false);
        setupRecyclerView((RecyclerView) rootView.findViewById(R.id.recyclelist));
        rootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent intent = FragmentDelegate.getIntent(getActivity(), 0, 0, "ZoneEditFragment", ZoneEditFragment.class.getName(), BaseActivity.class, null);
                startActivity(intent);
            }
        });
        initActionBar();
        return rootView;
    }

    private void setupRecyclerView( RecyclerView recyclerView) {
        mAdapter = new SimpleAdapter<ZoneModel>(ZoneModel.class) {
            @Override
            protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
                super.onBindViewHolder(holder, cursor);
                holder.itemView.setOnClickListener(ZoneListFragment.this);

            }
        };
        recyclerView.setAdapter(mAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.common_recycler_item_space);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoader();
        startZoneListRequest();
    }

    private void initActionBar() {
        if (mAbar != null) {
            mAbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            setTitle(R.string.zone_list_title);
            mAbar.setDisplayHomeAsUpEnabled(true);
            if (mToolBarDelegate != null) {
                mToolBarDelegate.getBackView().setVisibility(View.GONE);
            }
        }
    }

    private void initLoader(){
        Bundle args = new Bundle();
        startLoadLocal(args);
    }

    @Override
    protected int getLocalLoadId() {
        return Constants.LOADER.LOADER_ID_ZONE;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == getLocalLoadId()) {    // 子项Loader
            return new CursorLoader(getContext(), Zone.CONTENT_URI, null, null, null, null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        if(id == getLocalLoadId()) {    // 加载子项数据
            mAdapter.swapCursor((Cursor) data);
        }
    }

    private void startZoneListRequest(){
        ZoneListRequest request = ApiCreator.getInstance().createApi(ZoneListRequest.class);
        Call<BaseJsonBean<LocModel>> call = request.getZoneList(START_PAGE_INDEX,PAGE_LENGTH);
        call.enqueue(new BpCallback<BaseJsonBean<LocModel>>() {
            @Override
            public void onResponse(BaseJsonBean<LocModel> helpCenterModel) {
                if (helpCenterModel == null){
                    return;
                }
                LocModel model = helpCenterModel.getData();
                if (model == null || model.getZones() == null || model.getZones().size() == 0){
                    return;
                }
                ProviderHelper.clearZoneMap(getActivity());
                List<LocItem> list = model.getZones();
                for (int i = 0;i<list.size();i++) {
                    LocItem item = list.get(i);
                    ZoneHelper.saveRemoteId(getActivity(),String.valueOf(item.getId()), item.getName());
                }
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        ZoneModel model = (ZoneModel) v.getTag();
        String id = model.getRemoteId();
        String name = model.getName();
        LogUtil.d(Constants.DEBUG_TAG, "click zone " + id + " to open it");
        mIntent.putExtra(Constants.EXTRA.KEY_ID, id);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA.KEY_ID,id);
        bundle.putString(Constants.EXTRA.KEY_NAME,name);
        Intent intent = FragmentDelegate.getIntent(getActivity(), 0, 0, "SpotListFragment", SpotListFragment.class.getName(), BaseActivity.class, bundle);
        startActivity(intent);
//        startActivity(mIntent);
    }
}
