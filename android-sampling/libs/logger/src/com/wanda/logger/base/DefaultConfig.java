package com.wanda.logger.base;

import com.wanda.logger.toolbox.IConfig;

/**
 * Created by mengmeng on 16/6/13.
 */
public class DefaultConfig extends IConfig {
    @Override
    public String getFilePath() {
        return "indoor";
    }

    @Override
    public String getFileName() {
        return "log.txt";
    }

    @Override
    public String getPostFix() {
        return ".txt";
    }
}
