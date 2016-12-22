package com.feifan.locate.testkit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.feifan.locate.baseuilib.ToolbarActivity;
import com.feifan.locate.testkit.sensor.SensorAnalogFragment;
import com.feifan.locate.widget.BaseActivity;
import com.feifan.locate.widget.SimpleListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findView(R.id.main_grid);

        SimpleListAdapter<TestModel> adapter = new SimpleListAdapter<TestModel>(TestModel.class) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                holder.itemView.setOnClickListener(MainActivity.this);
            }
        };
        List<TestModel> testList = new ArrayList<>();
        testList.add(new TestModel("验证传感器组件", SensorAnalogFragment.class.getName()));
        adapter.setData(testList);

        recyclerView.setAdapter(adapter);

        mIntent = new Intent();
        mIntent.setClassName(getPackageName(), ToolbarActivity.class.getName());

    }

    @Override
    public void onClick(View view) {
        TestModel model = (TestModel)view.getTag();
        mIntent.putExtra(ToolbarActivity.EXTRA_KEY_FRAGMENT, model.fragmentName);
        startActivity(mIntent);
    }

    private static class TestModel extends SimpleListAdapter.ListModel {
        public String title;
        public String fragmentName;

        public TestModel(String title, String fragmentName) {
            this.title = title;
            this.fragmentName = fragmentName;
        }

        @Override
        public String toString() {
            return title;
        }
    }

}
