package com.feifan.locate.widget.cursorwork;

import android.database.Cursor;

/**
 * 游标数据适配器接口
 * <p>
 *     实现该接口的对象可在CursorLoader中使用
 * </p>
 *
 * Created by xuchunlei on 16/9/20.
 */
public interface ICursorAdapter {

    /**
     * 更换新的游标数据
     * @param newCursor
     * @return 与原游标相同，返回null，否则返回原游标
     */
    Cursor swapCursor(Cursor newCursor);
}
