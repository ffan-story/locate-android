package com.feifan.locate.widget.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by xuchunlei on 16/5/18.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

//        if(parent.getChildLayoutPosition(view) != 0){
//            outRect.top = space;
//            outRect.left = space;
//            outRect.right = space;
//            outRect.bottom = space;
//        }

        outRect.top = space;
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

    }
}
