package com.wosaitest.httpproxy;

/**
 * Created by yangzhixiang on 2017/6/22.
 */
public class KeyPair {

    private String path;
    private MethodType methodType;

    public enum MethodType {
        get, post, delete, patch, put,
    }

    public KeyPair(String path, MethodType methodType) {
        this.path = path.split("[?]")[0];
        this.methodType = methodType;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        KeyPair keyPair = (KeyPair) obj;
        if (keyPair.path.matches(path) && methodType == keyPair.methodType) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Path: " + path + "---> Method: " + methodType;
    }

    public String getPath() {
        return this.path;
    }

    public MethodType getMethodType() {
        return this.methodType;
    }

}
