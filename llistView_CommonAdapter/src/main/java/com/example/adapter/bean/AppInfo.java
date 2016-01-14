package com.example.adapter.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {

	private Drawable icon;
	private String appName;
	private String packageName;

	public AppInfo(Drawable icon, String appName, String packageName) {
		super();
		this.icon = icon;
		this.appName = appName;
		this.packageName = packageName;
	}

	public AppInfo() {
		super();
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
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

	@Override
	public String toString() {
		return "AppInfo [icon=" + icon + ", appName=" + appName
				+ ", packageName=" + packageName + "]";
	}

}
