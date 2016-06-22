package com.wanda.logger.file;

import android.text.TextUtils;

import com.wanda.logger.toolbox.IConfig;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by mengmeng on 16/6/8.
 */
public class CvsFileImpl extends IFile {


  public CvsFileImpl(IConfig config) {
    super(config);
  }

  @Override
  public void init() {

  }


  @Override
  public void write2File(List<String[]> list) {
    super.write2File(list);
    if (list == null || list.isEmpty()) {
      return;
    }
    FileWriter fw;
    BufferedWriter bfw = null;

      try {
          fw = new FileWriter(mLogFile);
          bfw = new BufferedWriter(fw);

          for (int i = 0; i < list.size(); i++) {
              String[] strs = list.get(i);
              if (strs != null && strs.length > 0){
                  for (int j = 0; j < strs.length; i++) {
                      String item = strs[j];
                      if(!TextUtils.isEmpty(item)){
                          if(j == strs.length -1) {
                              bfw.write(item);
                          }else {
                              bfw.write(item + ',');
                          }
                      }
                  }
                  bfw.newLine();
              }
          }
      } catch (IOException e) {
          e.printStackTrace();
      }finally {
          try {
              if(bfw != null) {
                  bfw.flush();
                  bfw.close();
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }


  }
}
