package com.atguigu.ms.bean;

import android.graphics.drawable.Drawable;

/**
 * 封装应用流量相关信息的实体类
 * 
 * @author 张晓飞
 *
 */
public class TrafficInfo {

	private String name;
	private Drawable icon;
	private long inSize;// 下载的流量
	private long outSize;// 上传的流量

	public TrafficInfo(String name, Drawable icon, long inSize, long outSize) {
		super();
		this.name = name;
		this.icon = icon;
		this.inSize = inSize;
		this.outSize = outSize;
	}

	public TrafficInfo() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return "TrafficInfo [name=" + name + ", icon=" + icon + ", inSize="
				+ inSize + ", outSize=" + outSize + "]";
	}

}
