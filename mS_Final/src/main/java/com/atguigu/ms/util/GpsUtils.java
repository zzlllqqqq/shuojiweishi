package com.atguigu.ms.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * GPS定位的工具类
 * @author 张晓飞
 *
 */
public final class GpsUtils {

	private static LocationManager manager;
	private static String safeNumber;
	private static LocationListener listener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {//位置改变了
			//得到经纬度
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			//发送给安全号码
			MSUtils.sendSms(safeNumber, latitude+" : "+longitude);
		}
	};
	
	/**
	 * gps定位
	 * @param context
	 * @param safeNumber
	 */
	public static void location(Context context, String safeNumber) {
		Log.e("TAG", "location()");
		if(manager==null) {
			manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			GpsUtils.safeNumber = safeNumber;
		}
		
		//得到定位信息对象
		Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location!=null) {
			//得到经纬度
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			//发送给安全号码
			MSUtils.sendSms(safeNumber, latitude+" : "+longitude);
		}
		
		//设置监听(自动获取位置, 并调用监听器的回调方法)
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, listener );
	}

}
