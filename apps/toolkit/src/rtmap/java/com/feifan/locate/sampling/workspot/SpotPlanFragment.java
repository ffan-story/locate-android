package com.feifan.locate.sampling.workspot;

import android.net.Uri;

import com.feifan.locate.sampling.SamplePlanFragment;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.rtm.frm.map.MapView;

/**
 * Created by xuchunlei on 2016/10/26.
 */

public class SpotPlanFragment extends SamplePlanFragment {

    @Override
    protected int getLoaderId() {
        return 0;
    }

    @Override
    protected Uri getContentUri() {
        return null;
    }

    @Override
    protected <A extends ICursorAdapter> A getAdapter() {
        return null;
    }

    @Override
    protected void onCreateLayer(MapView mapView) {

    }
}
