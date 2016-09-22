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
import com.feifan.locate.sampling.model.ZoneModel;
import com.feifan.locate.utils.IOUtils;
import com.feifan.locate.utils.ImageUtils;
import com.feifan.locate.utils.ScreenUtils;
import com.feifan.locate.widget.cursorwork.CursorPagerAdapter;
import com.feifan.planlib.PlanView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xuchunlei on 16/9/21.
 */
public class PlanPagerAdapter extends CursorPagerAdapter<PlanView> {

    public PlanPagerAdapter(Context context, Class<PlanView> clazz) {
        super(context, clazz);
    }

    @Override
    protected void renderView(PlanView view, Cursor cursor, int position) {
        InputStream is = null;
        try {
            ZoneModel zone = new ZoneModel(cursor);
            is = mContext.getAssets().open(zone.plan);
//            Drawable d = Drawable.createFromStream(is, null);
            BitmapFactory.Options options = ImageUtils.getOptionsFromStream(is);
            options.inSampleSize = ImageUtils.calculateInSampleSize(options,
                    ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenHeight() / 2);
            options.inJustDecodeBounds = false;
            view.setImageBitmap(BitmapFactory.decodeStream(is, null ,options));
//            view.setImageDrawable(d);
        }
        catch(IOException ex) {
            LogUtils.e(ex.getMessage());
            return;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
//        ImageView v = (ImageView)object;
//        ((BitmapDrawable)v.getDrawable()).getBitmap().recycle();
//        v.setImageDrawable(null);
    }


}
