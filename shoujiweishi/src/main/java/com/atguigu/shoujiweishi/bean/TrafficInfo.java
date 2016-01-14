package com.atguigu.shoujiweishi.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by admin on 2016/1/8.
 */
public class TrafficInfo {
    private String appName;
    private Drawable icon;
    private long inSize;
    private long outSize;

    public TrafficInfo(String appName, Drawable icon, long inSize, long outSize) {
        this.appName = appName;
        this.icon = icon;
        this.inSize = inSize;
        this.outSize = outSize;
    }

    public TrafficInfo() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getInSize() {
        return inSize;
    }

    public void setInSize(long inSize) {
        this.inSize = inSize;
    }

    public long getOutSize() {
        return outSize;
    }

    public void setOutSize(long outSize) {
        this.outSize = outSize;
    }

    @Override
    public String toString() {
        return "TrafficInfo{" +
                "appName='" + appName + '\'' +
                ", icon=" + icon +
                ", inSize=" + inSize +
                ", outSize=" + outSize +
                '}';
    }
}
