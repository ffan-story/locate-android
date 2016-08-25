package com.feifan.locate.sampling;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.locate.R;
import com.feifan.locate.utils.LogUtils;
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;
import com.feifan.locate.widget.cursorwork.SimpleCursorAdapter;
import com.feifan.locate.widget.plan.MarkLayer;
import com.feifan.locate.widget.ui.AbsSensorFragment;
import com.feifan.locate.provider.LocateData.Spot;
/**
 * A simple {@link Fragment} subclass.
 */
public class SpotDetailFragment extends AbsSensorFragment {

    public static final String EXTRA_KEY_MARKPOINT = "markpoint";

    // data
    private static final int LOADER_ID = 1; //// 使用异步加载组件时分配的ID，不能与其他数据使用的ID相同
    private SimpleCursorAdapter<DirectionModel> mAdapter;

    public SpotDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_spot_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        final MarkLayer.MarkPoint mark = args.getParcelable(EXTRA_KEY_MARKPOINT);
        final Intent data = new Intent();
        data.putExtra(EXTRA_KEY_MARKPOINT, mark);

        view.findViewById(R.id.spot_detail_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("Mark " + mark.toString() + " is removed!");
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });

        // 加载样本列表
        RecyclerView recyclerView = findView(R.id.spot_detail_sample_list);
        mAdapter = new SimpleCursorAdapter<DirectionModel>(DirectionModel.class){
            @Override
            protected void onBindViewHolder(ViewHolder holder, Cursor cursor) {
                super.onBindViewHolder(holder, cursor);
            }
        };
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected int getTitleResource() {
        return R.string.spot_detail_title_text;
    }

    @Override
    protected void onOrientationChanged(float radian) {
        // 更新样本列表
    }

    @Override
    protected int getLoaderId() {
        return LOADER_ID;
    }

    @Override
    protected Uri getContentUri() {
        return Spot.CONTENT_URI;
    }

    @Override
    protected <A extends RecyclerCursorAdapter> A getAdapter() {
        return (A)mAdapter;
    }

    public static class DirectionModel extends SimpleCursorAdapter.CursorModel {

        public float direction;
        public int sampleCount;

        public DirectionModel(Cursor cursor) {
            super(cursor);
            int idIndex = cursor.getColumnIndexOrThrow(Spot._ID);
            id = cursor.getInt(idIndex);
            int directionIndex = cursor.getColumnIndexOrThrow(Spot.D);
            direction = cursor.getFloat(directionIndex);

            // TODO 获取采样数量
            sampleCount = 0;
        }
    }
}
