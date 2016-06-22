package com.libs.base.model;

import com.libs.utils.NetUtils;

public class BaseBean{
    public String message;
    public String msg;
    public String status;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNetSucess(){
        return (NetUtils.NET_SUCESS_0.equals(status)) || (NetUtils.NET_SUCESS_200.equals(status));
    }
}
