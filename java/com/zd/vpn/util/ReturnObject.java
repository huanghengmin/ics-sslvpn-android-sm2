package com.zd.vpn.util;

/**
 * Created by Administrator on 15-12-11.
 */
public class ReturnObject {
    private boolean flag;
    private String msg;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ReturnObject() {
    }

    public ReturnObject(boolean flag, String msg) {
        this.flag = flag;
        this.msg = msg;
    }
}
