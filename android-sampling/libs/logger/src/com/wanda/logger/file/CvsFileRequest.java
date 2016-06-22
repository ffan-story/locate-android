package com.wanda.logger.file;

import android.text.TextUtils;

import com.wanda.logger.toolbox.IConfig;
import com.wanda.logger.toolbox.Request;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by mengmeng on 16/6/13.
 */
public class CvsFileRequest extends FileRequest<CVSModel> {
  public CvsFileRequest(IConfig config) {
    super(config);
  }

  @Override
  public void writeLog(CVSModel log) {
    if (log != null) {
      String[] header = log.getHeader();
      List<String[]> contentList = log.getList();
      if (contentList == null || contentList.isEmpty()) {
        return;
      }
      FileWriter fw;
      BufferedWriter bfw = null;
      try {
        fw = new FileWriter(mLogFile);
        bfw = new BufferedWriter(fw);
        for (int i = 0; i < header.length; i++) {
          String item = header[i];
          if (!TextUtils.isEmpty(item)) {
            if (i < header.length - 1) {
              bfw.write(item + ',');
            } else {
              bfw.write(item);
              bfw.newLine();
            }
          }
        }
        for (int i = 0; i < contentList.size(); i++) {
          String[] strs = contentList.get(i);
          if (strs != null && strs.length > 0) {
            for (int j = 0; j < strs.length; j++) {
              String item = strs[j];
              if (!TextUtils.isEmpty(item)) {
                if (j == strs.length - 1) {
                  bfw.write(item);
                } else {
                  bfw.write(item + ',');
                }
              }
            }
            bfw.newLine();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          if (bfw != null) {
            bfw.flush();
            bfw.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
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
  public int compareTo(Request<CVSModel> another) {
    return 0;
  }
}
