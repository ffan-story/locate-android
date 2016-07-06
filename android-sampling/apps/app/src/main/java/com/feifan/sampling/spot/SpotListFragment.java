package com.feifan.sampling.spot;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.feifan.sampling.http.ApiCreator;
import com.feifan.sampling.provider.ProviderHelper;
import com.feifan.sampling.provider.SampleData;
import com.feifan.sampling.scan.ScanFragment;
import com.feifan.sampling.spot.model.SpotItem;
import com.feifan.sampling.spot.model.SpotList;
import com.feifan.sampling.spot.request.SpotListRequest;
import com.feifan.sampling.widget.RecyclerCursorAdapter;
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
public class SpotListFragment extends CommonFragment<Cursor> implements View.OnClickListener {

    private SpotsAdapter mAdapter;
    /** 创建Loader时的参数－selection */
    protected static final String LOADER_KEY_SELECTION = "selection";
    /** 创建Loader时的参数－selectionArgs */
    protected static final String LOADER_KEY_SELECTION_ARGS = "selectionArgs";
    private int START_PAGE_INDEX = 1;
    private int PAGE_LENGTH = 20;
    private String mZoneId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_spot_list_layout, container, false);
        return rootView;
    }

    private void initArgus(){
        Bundle bundle = getArguments();
        if(bundle != null){
            mZoneId = bundle.getString(Constants.EXTRA.KEY_ID);
            String name = bundle.getString(Constants.EXTRA.KEY_NAME);
            setTitle(name);
        }
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
        initArgus();
        initLoader();
    }

    private void initLoader(){
        Bundle args = new Bundle();
        args.putString(LOADER_KEY_SELECTION, SampleData.Spot.ZONE + " = ?");
        args.putStringArray(LOADER_KEY_SELECTION_ARGS, new String[]{String.valueOf(mZoneId)});
        startLoadLocal(args);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclelist);
        mAdapter = new SpotsAdapter();
        recyclerView.setAdapter(mAdapter);
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.spot_fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpotPage();
            }
        });
    }

    private void startSpotPage(){
        Bundle bundle = new Bundle();
        bundle.putString("zoneid",mZoneId+"");
        Intent intent = FragmentDelegate.getIntent(getActivity(), 0, 0, "SpotEditFragment", SpotEditFragment.class.getName(), BaseActivity.class, bundle);
        startActivity(intent);
    }

    private void startSpoListRequest(){
        SpotListRequest request = ApiCreator.getInstance().createApi(SpotListRequest.class);
        Call<BaseJsonBean<SpotList>> call = request.getSpotList(Integer.valueOf(mZoneId),START_PAGE_INDEX,PAGE_LENGTH);
        call.enqueue(new BpCallback<BaseJsonBean<SpotList>>() {
            @Override
            public void onResponse(BaseJsonBean<SpotList> helpCenterModel) {
                if (helpCenterModel == null){
                    return;
                }
                SpotList model = helpCenterModel.getData();
                if (model == null || model.getSpots() == null || model.getSpots().size() == 0){
                    return;
                }
                ProviderHelper.clearZoneMap(getActivity());
                List<SpotItem> list = model.getSpots();
                for (int i = 0;i<list.size();i++) {
                    SpotItem item = list.get(i);
                    SpotHelper.saveRemoteId(getActivity(),String.valueOf(item.getX()),String.valueOf(item.getY()),String.valueOf(item.getD()),mZoneId,String.valueOf(item.getId()));
                }
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }
    @Override
    protected int getLocalLoadId() {
        return Constants.LOADER.LOADER_ID_ZONE;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == getLocalLoadId()) {    // 子项Loader
            String selection = args != null ? args.getString(LOADER_KEY_SELECTION) : null;
            String[] selectionArgs = args != null ? args.getStringArray(LOADER_KEY_SELECTION_ARGS) : null;
            return new CursorLoader(getContext(), SampleData.Spot.CONTENT_URI, null, selection, selectionArgs, null);
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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * 采集点数据适配器
     */
    public class SpotsAdapter extends RecyclerCursorAdapter<SpotsAdapter.ViewHolder> {

        @Override
        protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
            final SpotModel spot = SpotModel.from(cursor);
            holder.mCoordinateV.setText(spot.toString());
            holder.mDetailsV.setText(String.format("#beacons:%d\t\t\t\t#samples:%d", spot.beaconCount, spot.sampleCount));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.EXTRA_KEY_SPOT_ID, String.valueOf(spot.remoteid));
                    bundle.putString(Constants.EXTRA_KEY_SPOT_NAME, spot.toString());
                    bundle.putString(Constants.EXTRA_KEY_SPOT_DIRECTION, String.valueOf(spot.d));
                    bundle.putFloat(Constants.EXTRA_KEY_SPOT_X, spot.x);
                    bundle.putFloat(Constants.EXTRA_KEY_SPOT_Y, spot.y);
                    Intent intent = FragmentDelegate.getIntent(getActivity(), 0, 0, "ScanFragment", ScanFragment.class.getName(), BaseActivity.class, bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.spot_list_item, parent, false);
            return new ViewHolder(view);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mCoordinateV;
            public final TextView mDetailsV;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mCoordinateV = (TextView) view.findViewById(R.id.spot_item_coordinate);
                mDetailsV = (TextView) view.findViewById(R.id.spot_item_details);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mCoordinateV.getText() + "'";
            }
        }
    }
}
