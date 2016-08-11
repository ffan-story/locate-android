package com.feifan.locate.sampling;


import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.feifan.locate.R;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.cursorwork.RecyclerCursorAdapter;

import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpotPlanFragment extends AbsLoaderFragment {


    public SpotPlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spot_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView img = findView(R.id.spot_plan_img);


        try {
            // get input stream
            InputStream ims = getContext().getAssets().open("zone_2.png");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            img.setImageDrawable(d);
        }
        catch(IOException ex) {
            return;
        }
    }

    @Override
    protected int getLoaderId() {
        return 0;
    }

    @Override
    protected Uri getContentUri() {
        return null;
    }

    @Override
    protected <A extends RecyclerCursorAdapter> A getAdapter() {
        return null;
    }
}
