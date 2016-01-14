package com.atguigu.shoujiweishi.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by admin on 2016/1/4.
 */
public class AppInfo {
    private String packageName;
    private String appName;
    private Drawable icon;
    private boolean isSystem;//是否是系统应用

    public AppInfo(String packageName, String appName, Drawable icon, boolean isSystem) {
        this.packageName = packageName;
        this.appName = appName;
        this.icon = icon;
        this.isSystem = isSystem;
    }

    public AppInfo() {
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public boolean isSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", icon=" + icon +
                ", isSystem=" + isSystem +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppInfo appInfo = (AppInfo) o;

        return !(packageName != null ? !packageName.equals(appInfo.packageName) : appInfo.packageName != null);

    }

    @Override
    public int hashCode() {
        return packageName != null ? packageName.hashCode() : 0;
    }
}
