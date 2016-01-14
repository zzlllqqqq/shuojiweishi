package com.atguigu.weishi.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by admin on 2016/1/11.
 */
public class AppInfo {
    String appName;
    String packageName;
    Drawable icon;
    boolean isSystem;

    public AppInfo(String appName, String packageName, Drawable icon, boolean isSystem) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.isSystem = isSystem;
    }

    public AppInfo() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
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
