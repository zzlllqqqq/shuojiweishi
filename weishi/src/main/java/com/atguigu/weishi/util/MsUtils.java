package com.atguigu.weishi.util;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.atguigu.weishi.R;
import com.atguigu.weishi.bean.AppInfo;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2015/12/25.
 */
public final class MsUtils {


    public static String getVersion(Context context) {
        String versionName = "版本未知";
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static boolean isconnect(Context context) {
        boolean connect = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info != null) {
        connect = info.isConnected();
        }
        return connect;
    }

    public static void showMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String md5(String pwd) {
        StringBuffer sb = new StringBuffer();
        try {
            //创建用于加密的加密对象
            MessageDigest digest = MessageDigest.getInstance("md5");
            //将字符串转换为一个16位的byte[]
            byte[] bytes = digest.digest(pwd.getBytes("utf-8"));
            for(byte b : bytes) {//遍历
                //与255(0xff)做与运算(&)后得到一个255以内的数值
                int number = b & 255;//也可以& 0xff
                //转化为16进制形式的字符串, 不足2位前面补0
                String numberString = Integer.toHexString(number);
                if(numberString.length()==1) {
                    numberString = 0+numberString;
                }
                //连接成密文
                sb.append(numberString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getSimNumber(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getSimSerialNumber();
    }

    public static void sendSms(String number, String contant) {
        SmsManager.getDefault().sendTextMessage(number, null, contant, null, null);
    }

    public static void playAlert(Context context) {
        MediaPlayer player = MediaPlayer.create(context, R.raw.alert);
        player.setVolume(1, 1);
        player.setLooping(true);
        player.start();
    }

    public static void resetPhone(Context context) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        manager.wipeData(0);
    }

    public static void lockScreen(Context context) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        manager.lockNow();
        manager.resetPassword("123321", 0);
    }

    public static Map<Boolean, List<AppInfo>> getAllAppInfos(Context context) {
        Map<Boolean, List<AppInfo>> map = new HashMap<>();
        List<AppInfo> systemInfos = new ArrayList<>();
        List<AppInfo> userInfos = new ArrayList<>();
        map.put(true, systemInfos);
        map.put(false, userInfos);

        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();//匹配所有应用的主Activity
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo ri : resolveInfos) {
            //包名
            String packageName = ri.activityInfo.packageName;
            //应用图标
            Drawable icon = ri.loadIcon(packageManager);
            //应用名称
            String appName = ri.loadLabel(packageManager).toString();
            //是否是系统应用
            boolean isSystemApp = false;
            try {
                isSystemApp = isSystemApp(packageManager, packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            AppInfo appInfo = new AppInfo(packageName, appName, icon, isSystemApp);
            if (appInfo.isSystem()) {
                systemInfos.add(appInfo);
            } else {
                userInfos.add(appInfo);
            }
        }
        return map;
    }

    private static boolean isSystemApp(PackageManager pm, String packageName) throws Exception {
        PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
    }
}
