package com.feifan.sampling;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feifan.sampling.provider.SampleData.Spot;
import com.feifan.sampling.spot.SpotEditFragment;
import com.feifan.sampling.spot.SpotModel;
import com.feifan.sampling.util.LogUtil;
import com.feifan.sampling.widget.RecyclerCursorAdapter;
import com.libs.ui.activities.BaseActivity;
import com.libs.ui.fragments.FragmentDelegate;
import com.feifan.sampling.widget.SpaceItemDecoration;

/**
 * 采集点活动
 */
public class SpotListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private String TAG = SpotListActivity.class.getSimpleName();

    // 活动请求码－添加采集点
    private final static int REQUEST_CODE_ADD_SPOT = 1;

    /** 创建Loader时的参数－selection */
    protected static final String LOADER_KEY_SELECTION = "selection";
    /** 创建Loader时的参数－selectionArgs */
    protected static final String LOADER_KEY_SELECTION_ARGS = "selectionArgs";

    private SpotsAdapter mAdapter;

    private int mZoneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_list);

        // 标题
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // 访问添加采集点界面
        final Intent intent = new Intent(getApplicationContext(), TopBarActivity.class);
        intent.putExtra(TopBarActivity.EXTRA_NAME_FRAGMENT, SpotEditFragment.class.getName());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.spot_fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivityForResult(intent, REQUEST_CODE_ADD_SPOT);
                startSpotPage();
            }
        });

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView)recyclerView);

        // 初始化Loader
        mZoneId = getIntent().getIntExtra(Constants.EXTRA.KEY_ID, Constants.NO_INTEGER);
        if(mZoneId != Constants.NO_INTEGER) {
            // 设置参数
            Bundle args = new Bundle();
            args.putString(LOADER_KEY_SELECTION, Spot.ZONE + " = ?");
            args.putStringArray(LOADER_KEY_SELECTION_ARGS, new String[]{String.valueOf(mZoneId)});
            getSupportLoaderManager().initLoader(Constants.LOADER.LOADER_ID_SPOT, args, this);
        } else {
            LogUtil.e(TAG, "OnCreate:we got an invalid zone id!" + mZoneId);
        }
    }

    private void startSpotPage(){
        Bundle bundle = new Bundle();
        bundle.putString("zoneid",mZoneId+"");
        Intent intent = FragmentDelegate.getIntent(this, 0, 0, "SpotEditFragment", SpotEditFragment.class.getName(), BaseActivity.class, bundle);
        startActivity(intent);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mAdapter = new SpotsAdapter();
        recyclerView.setAdapter(mAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.common_recycler_item_space);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD_SPOT:
                if(resultCode == SpotEditFragment.RESPONSE_CODE_SPOT) {
                    float x = data.getFloatExtra(SpotEditFragment.EXTRA_NAME_X, 0f);
                    float y = data.getFloatExtra(SpotEditFragment.EXTRA_NAME_Y, 0f);
                    float d = data.getFloatExtra(SpotEditFragment.EXTRA_NAME_D, 0f);
//                    SpotModel spot = new SpotModel(x, y, d);

                    // 保存到数据库
                    ContentValues values = new ContentValues();
                    values.put(Spot.X, x);
                    values.put(Spot.Y, y);
                    values.put(Spot.D, d);
                    values.put(Spot.NAME, toString());
                    values.put(Spot.ZONE, mZoneId);
//                    spot.fill(values);

                    Uri spotUri = getContentResolver().insert(Spot.CONTENT_URI, values);
//                    spot.id = Integer.valueOf(spotUri.getLastPathSegment());
//                    Log.i(TAG, spot.toString() + "'id is " + spot.id);
                    LogUtil.i(TAG, "(" + x + "," + y + "," + d + ")'s id is " + spotUri.getLastPathSegment());
                }
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = args != null ? args.getString(LOADER_KEY_SELECTION) : null;
        String[] selectionArgs = args != null ? args.getStringArray(LOADER_KEY_SELECTION_ARGS) : null;
        return new CursorLoader(this, Spot.CONTENT_URI, null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG, "we got " + data.getCount() + " spots from database");
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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

                    Context context = v.getContext();
                    Intent intent = new Intent(context, SampleActivity.class);
                    intent.putExtra(Constants.EXTRA_KEY_SPOT_ID, spot.id);
                    intent.putExtra(Constants.EXTRA_KEY_SPOT_NAME, spot.toString());
                    context.startActivity(intent);
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
