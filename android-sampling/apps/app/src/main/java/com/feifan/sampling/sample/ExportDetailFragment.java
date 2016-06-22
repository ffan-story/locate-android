package com.feifan.sampling.sample;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.feifan.sampling.provider.SampleData.Sample;
import com.feifan.sampling.provider.SampleData.SampleDetail;
import com.feifan.sampling.util.ProviderUtil;
import com.feifan.sampling.widget.AbsLoaderFragment;
import com.feifan.sampling.widget.RecyclerCursorAdapter;
import com.libs.utils.DateTimeUtils;
import com.feifan.sampling.widget.SpaceItemDecoration;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExportDetailFragment extends AbsLoaderFragment  {

    private static final String TAG = "SampleDetailFragment";

    private SampleDetailAdapter mAdapter;

    private String mSpotName;

    public ExportDetailFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLoaderId() {
        return Constants.LOADER.LOADER_ID_SAMPLE_DETAIL;
    }

    @Override
    protected Uri getContentUri() {
        return SampleDetail.CONTENT_URI;
    }

    @Override
    protected <A extends RecyclerCursorAdapter> A getAdapter() {
        return (A)mAdapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(Constants.EXTRA_KEY_SPOT_ID)) {
            final int spotId = getArguments().getInt(Constants.EXTRA_KEY_SPOT_ID);
            mSpotName = getArguments().getString(Constants.EXTRA_KEY_SPOT_NAME);
            Log.d(TAG, "onCreate:we got spot'id is " + spotId + " from arguments");
            // 重置参数
            getArguments().clear();
            getArguments().putString(LOADER_KEY_SELECTION, Sample.SPOT + " = ?");
            getArguments().putStringArray(LOADER_KEY_SELECTION_ARGS, new String[]{String.valueOf(spotId)});
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        setupRecyclerView((RecyclerView)view);
        return view;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SampleDetailAdapter();
        recyclerView.setAdapter(mAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.common_recycler_item_space);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick:" + item.getTitle());
        String time = DateTimeUtils.getCurrentTime("MM-dd hh:mm:ss");
        File file = ProviderUtil.exportToCSV(mAdapter.getCursor(), getActivity(), "sample" + mSpotName+"-"+time + ".csv");
        if(file != null) {
            Toast.makeText(getContext(), "export data to" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    protected List<MenuInfo> getMenuList() {
        List<MenuInfo> infoList = super.getMenuList();
        MenuInfo info = new MenuInfo(R.id.menu_sample_detail_export, Constants.NO_INTEGER, R.string.sample_detail_menu_export_text);
        infoList.add(info);
        return infoList;
    }

    /**
     * 采集点数据适配器
     */
    public class SampleDetailAdapter extends RecyclerCursorAdapter<SampleDetailAdapter.ViewHolder> {

        @Override
        protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
            final SampleDetailModel detail = new SampleDetailModel(cursor);
            holder.mDetailsV.setText(detail.toString());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simple_list_item, parent, false);
            return new ViewHolder(view);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mDetailsV;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mDetailsV = (TextView) view.findViewById(R.id.simple_item_content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mDetailsV.getText() + "'";
            }
        }
    }

}
