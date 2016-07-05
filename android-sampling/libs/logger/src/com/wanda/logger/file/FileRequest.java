package com.wanda.logger.file;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.wanda.logger.toolbox.IConfig;
import com.wanda.logger.toolbox.Request;

import java.io.File;
import java.io.IOException;

/**
 * Created by mengmeng on 16/6/13.
 */
public abstract class FileRequest<T> extends Request<T> {

  protected File mLogFile;

  public FileRequest(IConfig config) {
    super(config);
    init();
  }

  protected void init() {
    File path =
        new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
            + mConfig.getFilePath() + File.separator);
    if (!path.exists()) {
      Log.d("TestFile", "Create the path:" + path.getAbsolutePath());
      path.mkdir();
    }
    String fileName = mConfig.getFileName()+mConfig.getPostFix();
    if (TextUtils.isEmpty(fileName) || !fileName.contains(".")) {
      throw new IllegalArgumentException("the filename can not be null or filename format illegal");
    }
    mLogFile =
        new File(path.getPath()+ File.separator+ fileName);
    if (!mLogFile.exists()) {
      try {
        mLogFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
