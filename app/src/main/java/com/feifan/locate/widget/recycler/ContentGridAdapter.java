package com.feifan.locate.widget.recycler;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feifan.locate.utils.ScreenUtils;

import java.util.List;

/**
 * Created by xuchunlei on 16/10/10.
 */

public class ContentGridAdapter<M extends IContentModel> extends RecyclerView.Adapter<ContentGridAdapter.GridHolder> {

    @IdRes
    private static final int ID_TITLE = 1;
    @IdRes
    private static final int ID_CONTENT = 2;

    private Class<M> mClazz;
    private List<M> mData;

    public ContentGridAdapter(Class<M> clazz) {
        mClazz = clazz;
    }

    @Override
    public GridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = createView(parent.getContext());
        return new GridHolder(v);
    }

    @Override
    public void onBindViewHolder(GridHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void setData(List<M> data) {
        mData = data;
        notifyDataSetChanged();
    }

    private View createView(Context context) {

        final int itemHeight = ScreenUtils.getScreenHeight() / 4;

        LinearLayout layout = new LinearLayout(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                itemHeight
        );
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.WHITE);

        // title
        TextView titleV = new TextView(context);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleV.setLayoutParams(titleParams);
        titleV.setId(ID_TITLE);
        titleV.setGravity(Gravity.CENTER);
        titleV.setTextColor(Color.WHITE);
        titleV.setPadding(5, 5, 5, 5);
        titleV.setBackgroundColor(Color.argb(0xa0, 0xa0, 0xa0, 0xa0));
        titleV.setLines(2);
        layout.addView(titleV);

        // content
        TextView contentV = new TextView(context);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contentV.setLayoutParams(contentParams);
        contentV.setId(ID_CONTENT);
        layout.addView(contentV);

        return layout;
    }

    public static class GridHolder<M extends IContentModel> extends RecyclerView.ViewHolder {

        public GridHolder(View itemView) {
            super(itemView);
        }

        public void bind(M model) {
            TextView titleV = (TextView) itemView.findViewById(ID_TITLE);
            titleV.setText(model.getTitle());
            TextView contentV = (TextView)itemView.findViewById(ID_CONTENT);
            contentV.setText(model.getContent());
        }
    }
}
