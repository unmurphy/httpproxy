package com.wosaitest.httpproxy;

/**
 * Created by yangzhixiang on 2017/6/27.
 */
public class DataPair {

    private int wait;
    private Object resp;

    public DataPair(int wait, Object resp) {
        this.wait = wait;
        this.resp = resp;
    }

    public int getWait() {
        return this.wait;
    }

    public Object getValue() {
        return this.resp;
    }
}
