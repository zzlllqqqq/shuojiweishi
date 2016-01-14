package com.atguigu.shoujiweishi.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by admin on 2015/12/28.
 */
public class GpsUtils {

    private static LocationManager locationManager;
    private static String number;
    private static LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            MSUtils.sendSms(number, latitude + "-" + longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * 进行gps定位
     * @param context
     * @param number
     */
    public static void location(Context context, String number) {

        if(locationManager == null) {
            GpsUtils.number = number;
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            MSUtils.sendSms(number, latitude + "-" + longitude);
        }


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 10, listener);
    }
}
