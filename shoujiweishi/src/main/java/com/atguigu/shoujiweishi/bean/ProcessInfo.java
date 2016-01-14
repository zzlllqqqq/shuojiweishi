package com.atguigu.shoujiweishi.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by admin on 2016/1/6.
 * 应用进程信息实体类
 */
public class ProcessInfo {

    private String appName;
    private String PackageName;
    private Drawable icon;
    private boolean system;
    private long memInfoSize;//占用内存的大小字节数
    private boolean checked;//是否被选中

    public ProcessInfo() {
    }

    public ProcessInfo(String appName, String packageName, Drawable icon, boolean system, long memInfoSize) {
        this.appName = appName;
        PackageName = packageName;
        this.icon = icon;
        this.system = system;
        this.memInfoSize = memInfoSize;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public long getMemInfoSize() {
        return memInfoSize;
    }

    public void setMemInfoSize(long memInfoSize) {
        this.memInfoSize = memInfoSize;
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "appName='" + appName + '\'' +
                ", PackageName='" + PackageName + '\'' +
                ", icon=" + icon +
                ", system=" + system +
                '}';
    }
}
