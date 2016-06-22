package com.feifan.sampling.sample;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.feifan.sampling.provider.SampleData.Sample;
import com.feifan.sampling.widget.AbsLoaderFragment;
import com.feifan.sampling.widget.RecyclerCursorAdapter;
import com.feifan.sampling.widget.SpaceItemDecoration;

/**
 * 样本界面
 * <pre>
 *     展示采样数据
 * </pre>
 */
public class SampleFragment extends AbsLoaderFragment {

    // 样本数据适配器
    private SampleAdapter mAdapter;

//    private RecyclerView mList;

    @Override
    protected int getLoaderId() {
        return Constants.LOADER.LOADER_ID_SAMPLE;
    }

    @Override
    protected Uri getContentUri() {
        return Sample.CONTENT_URI;
    }

    @Override
    protected <A extends RecyclerCursorAdapter> A getAdapter() {
        return (A)mAdapter;
    }

    private OnDataChangeListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnDataChangeListener) {
            mListener = (OnDataChangeListener)context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(Constants.EXTRA_KEY_SPOT_ID)) {

            final int spotId = getArguments().getInt(Constants.EXTRA_KEY_SPOT_ID);

            // 重置参数
            getArguments().clear();
            getArguments().putString(LOADER_KEY_SELECTION, Sample.SPOT + " = ?");
            getArguments().putStringArray(LOADER_KEY_SELECTION_ARGS, new String[]{String.valueOf(spotId)});

            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        setupRecyclerView((RecyclerView) rootView);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SampleAdapter();
//        mList = recyclerView;
        recyclerView.setAdapter(mAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.common_recycler_item_space);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    @Override
    protected void onPostLoad(Cursor data) {
        super.onPostLoad(data);
        if(data != null) {
            mListener.onNotifyChange(data.getCount());
        }
    }

    public interface OnDataChangeListener {
        void onNotifyChange(int totalSize);
    }

    /**
     * 样本数据适配器
     */
    private class SampleAdapter extends RecyclerCursorAdapter<SampleFragment.ViewHolder> {

        @Override
        protected void onBindViewHolder(SampleFragment.ViewHolder holder, Cursor cursor) {
            final SampleModel sample = SampleModel.from(cursor);
            holder.bind(sample);
//            holder.mDetailsV.setText(String.format("#beacons:%d\t\t\t\t#samples:%d", spot.beaconCount, spot.sampleCount));

//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, SampleActivity.class);
//                    Bundle args = new Bundle();
//                    args.putInt(SampleFragment.EXTRA_KEY_SPOT_ID, spot.id);
//                    intent.putExtra(AbsLoaderFragment.EXTRA_KEY_LOADER_ARGS, args);
//                    context.startActivity(intent);
//                }
//            });
        }

        @Override
        public SampleFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simple_list_item, parent, false);
            return new ViewHolder(view);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mContentV;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentV = (TextView)itemView.findViewById(R.id.simple_item_content);
        }

        public void bind(SampleModel sample) {
            mContentV.setText(sample.toString());
        }
    }
}
