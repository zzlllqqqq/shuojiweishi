package com.atguigu.ms.bean;

import android.graphics.drawable.Drawable;

/**
 * 封装应用信息的类
 * 
 * @author 张晓飞
 *
 */
public class AppInfo {

	private String packageName;
	private String appName;
	private Drawable icon;
	private boolean isSystem;// 标识是否是系统应用的信息

	public AppInfo(String packageName, String appName, Drawable icon,
			boolean isSystem) {
		super();
		this.packageName = packageName;
		this.appName = appName;
		this.icon = icon;
		this.isSystem = isSystem;
	}

	public AppInfo() {
		super();
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

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	@Override
	public String toString() {
		return "AppInfo [packageName=" + packageName + ", appName=" + appName
				+ ", icon=" + icon + ", isSystem=" + isSystem + "]";
	}

	
	//根据packageName来重写hashCode()和equals()
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppInfo other = (AppInfo) obj;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		return true;
	}
	
	

}
