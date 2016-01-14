package com.atguigu.shoujiweishi.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
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
import android.net.TrafficStats;
import android.os.Build;
import android.os.Debug;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.widget.Toast;

import com.atguigu.shoujiweishi.R;
import com.atguigu.shoujiweishi.bean.AppInfo;
import com.atguigu.shoujiweishi.bean.ProcessInfo;
import com.atguigu.shoujiweishi.bean.TrafficInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2015/12/25.
 * 当前应用的通用工具类
 */
public final class MSUtils {

    /**
     * 得到当前应用的版本号
     * @param context
     * @return
     */
    public static String getVersion(Context context){
        String version = "版本未知";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 检查手机是否联网
     * @param context
     * @return
     */
    public static boolean isconnect(Context context) {
        boolean connect = false;
        ConnectivityManager manamger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manamger.getActiveNetworkInfo();
        if (info != null){
            connect = info.isConnected();
        }
        return connect;
    }

    /**
     * 显示文本小提示
     * @param context
     * @param msg
     */
    public static void showMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * md5加密
     * @param pwd
     * @return
     */
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


    /**
     * 向指定号码发送短信
     * @param number
     * @param contant
     */
    public static void sendSms(String number, String contant) {
        SmsManager.getDefault().sendTextMessage(number, null, contant, null, null);
    }

    /**
     * 播放警报音乐
     * @param context
     */
    public static void playAlert(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alert);
        mediaPlayer.setVolume(1, 1);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    /**
     *远程销毁数据
     * @param context
     */
    public static void resetPhone(Context context) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        manager.wipeData(0);
    }

    /**
     * 远程锁屏
     * @param context
     */
    public static void lockScreen(Context context) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        manager.resetPassword("1111", 0);
        manager.lockNow();
    }

    /**
     * 判断服务是否已经开启
     * @param context
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(Integer.MAX_VALUE); //取出所有运行的
        for(ActivityManager.RunningServiceInfo info : runningServices) {
            String serviceClassName = info.service.getClassName();
            if(serviceClassName.equals(className)) {
                return true;
            }
        }
        return false;
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

    /**
     * 判断是否是系统应用
     * @param pm
     * @param packageName
     * @return
     * @throws Exception
     */
    private static boolean isSystemApp(PackageManager pm, String packageName) throws Exception {
        PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
    }

    /**
     * 得到所有运行的进程信息的集合
     * @param context
     * @param systemProcessInfos
     * @param userProcessInfos
     */
    public static void getAllProcessInfos(Context context,
                    List<ProcessInfo> systemProcessInfos, List<ProcessInfo> userProcessInfos) {
        SystemClock.sleep(2000);

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();

        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            ProcessInfo info = new ProcessInfo();
            // 包名
            String packageName = processInfo.processName;
            //排除当前应用
            if(packageName.equals(context.getPackageName())) {
                //跳过本次循环直接进入下次循环
                continue;
            }

            info.setPackageName(packageName);
            // 应用占用的内存  bit/byte
            Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{processInfo.pid})[0];
            long memInfoSize = memoryInfo.getTotalPrivateDirty() * 1024;
            info.setMemInfoSize(memInfoSize);
            try {
                // 图标
                Drawable icon = pm.getPackageInfo(packageName, 0).applicationInfo.loadIcon(pm);
                info.setIcon(icon);
                // 应用名称
                String name = pm.getPackageInfo(packageName, 0).applicationInfo
                        .loadLabel(pm).toString();
                info.setAppName(name);
                // 是否是系统应用进程
                int flag = pm.getPackageInfo(packageName, 0).applicationInfo.flags;
                if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {// 用户进程
                    info.setSystem(false);
                } else {// 系统进程
                    info.setSystem(true);
                }
            } catch (Exception e) {//根据包名得到不到PackageInfo
                e.printStackTrace();
                info.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                info.setAppName(packageName);
                info.setSystem(true);
            }
            //不同类型的Info保存到不同的集合中
            if(info.isSystem()) {
                systemProcessInfos.add(info);
            } else {
                userProcessInfos.add(info);
            }
        }
    }

    /**
     * 得到手机中可用内存的大小
     */
    public static long getAvailMem(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    /**
     * 得到手机中总内存的大小
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getTotalMem(Context context) {
        long totalMem = 0;
        int sysVersion = Build.VERSION.SDK_INT; // 得到当前系统的版本号
        // 下面的方式只能在JELLY_BEAN(16)及以上版本才有用
        if (sysVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(memoryInfo);
            totalMem = memoryInfo.totalMem;
        } else {
            try { // 在版本小于16时, 读取/proc/meminfo文件的第一行来获取总大小
                File file = new File("/proc/meminfo");
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(fis));

                String result = reader.readLine();// MemTotal: 510484 kB
                result = result.substring(result.indexOf(":") + 1,
                        result.indexOf("k")).trim();// 510484

                reader.close();
                totalMem = Integer.parseInt(result) * 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return totalMem;
    }

    /**
     * 格式化大小
     * @param context
     * @param byteSize
     * @return
     */
    public static String formatSize(Context context, long byteSize) {
        return Formatter.formatFileSize(context, byteSize);
    }


    /**
     * 得到当前进程数
     * @param context
     * @return
     */
    public static int getProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        return processInfos.size();
    }

    /**
     * 杀死进程
     * @param context
     */
    public static void killProcess(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();

        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            ProcessInfo info = new ProcessInfo();
            // 包名
            String packageName = processInfo.processName;
            //排除当前应用
            if (packageName.equals(context.getPackageName())) {
                continue;
            }

            am.killBackgroundProcesses(packageName);
        }
    }

    /**
     * 得到所应用流量相关信息对象集合
     *
     * @param context
     * @return
     */
    public static List<TrafficInfo> getAllTrafficInfos(Context context) {
        List<TrafficInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> infos = pm.getInstalledApplications(0);
        for (ApplicationInfo info : infos) {
            TrafficInfo trafficInfo = new TrafficInfo();
            int uid = info.uid;
            //icon
            Drawable icon = info.loadIcon(pm);
            trafficInfo.setIcon(icon);
            //appName
            String appName = info.loadLabel(pm).toString();
            trafficInfo.setAppName(appName);
            //inSize 下载
            long inSize = TrafficStats.getUidRxBytes(uid);
            trafficInfo.setInSize(inSize);
            //outSize 上传
            long outSize = TrafficStats.getUidTxBytes(uid);
            trafficInfo.setOutSize(outSize);
            list.add(trafficInfo);
        }
        return list;
    }
}
