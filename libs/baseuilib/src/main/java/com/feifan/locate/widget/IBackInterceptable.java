package com.feifan.locate.widget;

import android.content.Intent;

/**
 * 返回操作拦截接口
 * <p>
 *     实现此接口的对象可以拦截返回操作，目前处理此接口行为的对象有：
 *     （1）ToolbarActivity
 * </p>
 *
 * Created by xuchunlei on 16/9/12.
 */
public interface IBackInterceptable {

    /**
     * 获得返回结果
     * <p>
     *     用于使用startActivityForResult方式启动的活动返回结果
     * </p>
     * @return
     */
    Intent getResult();

    /**
     * 返回操作是否生效
     *
     * @return
     */
    boolean isBackEnabled();
}
