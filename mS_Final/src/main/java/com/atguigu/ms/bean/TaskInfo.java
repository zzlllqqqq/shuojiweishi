package com.atguigu.ms.bean;

import android.graphics.drawable.Drawable;

/**
 * 封装进程信息的类
 * 
 * @author 张晓飞
 *
 */
public class TaskInfo {

	private String packageName;
	private String appName;
	private Drawable icon;
	private long memInfoSize;// 占用的内存大小
	private boolean isSystem;// 是否是系统应用进程
	private boolean isChecked; // 标识Checkbox是否应该选中

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
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

	public long getMemInfoSize() {
		return memInfoSize;
	}

	public void setMemInfoSize(long memInfoSize) {
		this.memInfoSize = memInfoSize;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

}
