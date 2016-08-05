package com.feifan.locate.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.support.annotation.XmlRes;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;

import com.feifan.locate.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 封装了资源操作的工具类
 * Created by xuchunlei on 16/8/4.
 */
public class BottomBarUtils {
    private static final String TAG = "BottomBarUtils";

    /** 非法整型，用于处理默认值或无意义的赋值 */
    public static final int NO_INTEGER = -1;

    // XML配置文件的标签tabs
    private static final String TAG_TABS = "tabs";
    // XML配置文件的标签tab
    private static final String TAG_TAB = "tab";

    /**
     * 实例化底部标签项
     * @param context
     * @param tabRes
     * @return
     */
    static ArrayList<BottomItem> loadTabs(Context context, @XmlRes int tabRes) {
        ArrayList<BottomItem> tabs = new ArrayList<>();

        // 解析描述标签项的xml文件
        try {
            XmlResourceParser parser = context.getResources().getXml(tabRes);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            beginDocument(parser, TAG_TABS);

            final int depth = parser.getDepth();


            int type;
            // 遍历条件：元素深度大于跟节点深度（一般为0）或未遍历到事件结束标签，且未遍历到文档末尾
            while (((type = parser.next()) != XmlPullParser.END_TAG ||
                    parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

                // 寻找事件开始标签
                if (type != XmlPullParser.START_TAG) {
                    continue;
                }
                final String name = parser.getName();

                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.tab);
                if(TAG_TAB.equals(name)) { // 标签项
                    int titleResId =  a.getResourceId(R.styleable.tab_item_title, NO_INTEGER);
                    int iconResId = a.getResourceId(R.styleable.tab_item_icon, NO_INTEGER);
                    String fragmentName = a.getString(R.styleable.tab_item_fragment);

                    // 实例化一个tab
                    BottomItem tab;
                    if(fragmentName != null) {
                        tab = new BottomFragmentItem(iconResId, titleResId, fragmentName);
                    } else {
                        tab = new BottomItem(iconResId, titleResId);
                    }

                    tabs.add(tab);
                    Log.d(TAG, tab.toString());
                }
                a.recycle();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tabs;
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    /**
     * dp转像素
     * @param context
     * @param dp
     * @return
     */
    static int dpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();

        try {
            return (int) (dp * (metrics.densityDpi / 160f));
        } catch (NoSuchFieldError ignored) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
        }
    }

    /**
     * 获取主题中定义的颜色
     * @param context
     * @param color
     * @return
     */
    static int getColor(Context context, int color) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(color, tv, true);
        return tv.data;
    }

    // 定位到文档开始位置
    private static final void beginDocument(XmlPullParser parser, String firstElementName)
            throws XmlPullParserException, IOException {
        int type;
        while ((type = parser.next()) != XmlPullParser.START_TAG
                && type != XmlPullParser.END_DOCUMENT) {
            ;
        }

        if (type != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("No start tag found");
        }

        if (!parser.getName().equals(firstElementName)) {
            throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() +
                    ", expected " + firstElementName);
        }
    }
}
