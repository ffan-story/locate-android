package com.wanda.logger.file;

import com.wanda.logger.toolbox.IConfig;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mengmeng on 16/6/13.
 */
public class TxtFileRequest extends FileRequest {
    public TxtFileRequest(IConfig config) {
        super(config);
    }

    @Override
    public void writeLog(Object log) {
        if (log != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(mLogFile, true);
                StringBuffer sb=new StringBuffer();
                sb.append(log);
                outputStream.write(sb.toString().getBytes("utf-8"));
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(Object another) {
        return 0;
    }
}
