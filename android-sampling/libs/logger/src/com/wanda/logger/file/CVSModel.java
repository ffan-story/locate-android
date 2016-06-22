package com.wanda.logger.file;

import java.util.List;

/**
 * Created by mengmeng on 16/6/17.
 */
public class CVSModel {
    private String[] header;
    private List<String[]> list;

    public String[] getHeader() {
        return header;
    }

    public void setHeader(String[] header) {
        this.header = header;
    }

    public List<String[]> getList() {
        return list;
    }

    public void setList(List<String[]> list) {
        this.list = list;
    }
}
