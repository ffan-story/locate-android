package com.feifan.locate.locating;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.R;
import com.feifan.locate.sampling.model.ZoneModel;
import com.feifan.locate.utils.IOUtils;
import com.feifan.locate.utils.ImageUtils;
import com.feifan.locate.utils.ScreenUtils;
import com.feifan.locate.widget.cursorwork.CursorPagerAdapter;
import com.feifan.planlib.ILayer;
import com.feifan.planlib.PlanView;
import com.feifan.planlib.layer.TraceLayer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuchunlei on 16/9/21.
 */
public class PlanPagerAdapter extends CursorPagerAdapter<PlanView> {

    Bitmap bmp;

    public PlanPagerAdapter(Context context, Class<PlanView> clazz) {
        super(context, clazz);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.plan_location);
    }

    @Override
    protected void renderView(PlanView view, Cursor cursor, int position) {
        InputStream is = null;
        try {
            ZoneModel zone = new ZoneModel(cursor);
            is = mContext.getAssets().open(zone.plan);
            BitmapFactory.Options options = ImageUtils.getOptionsFromStream(is);
            options.inSampleSize = ImageUtils.calculateInSampleSize(options,
                    ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenHeight() / 2);
            options.inJustDecodeBounds = false;
            view.setImageBitmap(BitmapFactory.decodeStream(is, null ,options));

            // 添加定位图层
            TraceLayer layer = new TraceLayer();
            layer.setDrawBitmap(bmp);
            view.addLayer(layer);
            view.setPlanScale(zone.scale);

        }
        catch(IOException ex) {
            LogUtils.e(ex.getMessage());
            return;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

}
