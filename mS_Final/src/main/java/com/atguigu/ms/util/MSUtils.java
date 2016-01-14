package com.atguigu.ms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Debug.MemoryInfo;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.widget.Toast;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.AppInfo;
import com.atguigu.ms.bean.TaskInfo;
import com.atguigu.ms.bean.TrafficInfo;

/**
 * 应用通用工具类
 * @author 张晓飞
 *
 */
public class MSUtils {

	/**
	 * 显示文本小提示
	 * @param context
	 * @param string
	 */
	public static void showMsg(Context context, String msg) {
		Toast.makeText(context, msg, 0).show();
	}
	
	/**
	 * 将一个字符串(明文)用md5加密, 返回密文
	 * @param string
	 * @return
	 */
	public static String md5(String string) {
		StringBuffer sb = new StringBuffer();
		try {
			// 创建用于加密的加密对象
			MessageDigest digest = MessageDigest.getInstance("md5");
			// 将字符串转换为一个16位的byte[]
			byte[] bytes = digest.digest(string.getBytes("utf-8"));
			for (byte b : bytes) {// 遍历
				// 与255(0xff)做与运算(&)后得到一个255以内的数值
				int number = b & 255;// 也可以& 0xff
				// 转化为16进制形式的字符串, 不足2位前面补0
				String numberString = Integer.toHexString(number);
				if (numberString.length() == 1) {
					numberString = 0 + numberString;
				}
				// 连接成密文
				sb.append(numberString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 得到SIM卡的序列号
	 * @param context
	 * @return
	 */
	public static String getSimNumber(Context context) {
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getSimSerialNumber();
		
	}

	/**
	 * 发送短信
	 * @param safeNumber
	 */
	public static void sendSms(String safeNumber) {
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(safeNumber, null, "Sim chaged, care!", null, null);
	}
	
	/**
	 * 发送短信
	 * @param safeNumber
	 */
	public static void sendSms(String safeNumber, String msg) {
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(safeNumber, null, msg, null, null);
	}

	/**
	 * 对手机锁屏
	 * @param context
	 */
	public static void lock(Context context) {
		DevicePolicyManager manager = (DevicePolicyManager) 
				context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		manager.resetPassword("123456", 0);//设置密码
		manager.lockNow();//立即锁屏
	}

	/**
	 * 播放警报音乐
	 * @param context
	 */
	public static void playAlarm(Context context) {
		MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alert);
		mediaPlayer.setLooping(true);//循环播放
		mediaPlayer.setVolume(1, 1);//设置音量最大
		mediaPlayer.start();//播放
	}

	/**
	 * 删除手机数据
	 * @param context
	 */
	public static void reset(Context context) {
		DevicePolicyManager manager = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		//清理数据
		manager.wipeData(0);

	}
	
	/**
	 * 判断指定的服务是否开启
	 * @param context
	 * @param className
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String className) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am
				.getRunningServices(Integer.MAX_VALUE); // 取出所有运行的
		for (RunningServiceInfo info : runningServices) {
			String serviceClassName = info.service.getClassName();
			if (serviceClassName.equals(className)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 得到手机中所有应用信息的集合
	 */
	public static Map<Boolean, List<AppInfo>> getAllAppInfos(Context context) {
		
		SystemClock.sleep(1000);
		
		Map<Boolean, List<AppInfo>> map = new HashMap<Boolean, List<AppInfo>>();
		List<AppInfo> systemInfos = new ArrayList<AppInfo>();
		map.put(true, systemInfos);
		List<AppInfo> userInfos = new ArrayList<AppInfo>();
		map.put(false, userInfos);
		
		//读取手机中所有应用信息数据
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
		for(ResolveInfo ri : resolveInfos) {
			//packageName
			String packageName = ri.activityInfo.packageName;
			//appName
			String appName = ri.loadLabel(packageManager).toString();
			//icon
			Drawable icon = ri.loadIcon(packageManager);
			//isSystem
			boolean isSystem = true;
			try {
				isSystem = isSystemApp(packageManager, packageName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			AppInfo appInfo = new AppInfo(packageName, appName, icon, isSystem);
			if(isSystem) {
				systemInfos.add(appInfo);
			} else {
				userInfos.add(appInfo);
			}
		}

		return map;
	}
	
	/**
	 * 判断指定包名所对应的应用是否是系统应用
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
	 * 得到所有正在运行的进程的信息集合
	 * @param context
	 * @param systemtaskInfos
	 * @param usertaskInfos
	 */
	public static void getAllTaskInfos(Context context,
			List<TaskInfo> systemtaskInfos, List<TaskInfo> usertaskInfos) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		PackageManager pm = context.getPackageManager();

		for (RunningAppProcessInfo processInfo : processInfos) {
			TaskInfo taskInfo = new TaskInfo();
			// 包名
			String packageName = processInfo.processName;
			//过滤当前应用
			if(packageName.equals(context.getPackageName())) {
				continue;//当前应用的进程信息不要保存到集合中
			}
			
			taskInfo.setPackageName(packageName);
			// 应用占用的内存
			MemoryInfo memoryInfo = am
					.getProcessMemoryInfo(new int[] { processInfo.pid })[0];
			long memInfoSize = memoryInfo.getTotalPrivateDirty() * 1024; //memory  byte
			taskInfo.setMemInfoSize(memInfoSize);
			try {
				// 图标
				Drawable icon = pm.getPackageInfo(packageName, 0).applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				// 应用名称
				String name = pm.getPackageInfo(packageName, 0).applicationInfo.loadLabel(pm).toString();
				taskInfo.setAppName(name);
				// 是否是系统应用进程
				int flag = pm.getPackageInfo(packageName, 0).applicationInfo.flags;
				if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {// 用户进程
					taskInfo.setSystem(false);
				} else {// 系统进程
					taskInfo.setSystem(true);
				}
			} catch (NameNotFoundException e) {//根据包名得到不到PackageInfo
				//e.printStackTrace();
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
				taskInfo.setAppName(packageName);
				taskInfo.setSystem(true);
			}
			//不同类型的Info保存到不同的集合中
			if(taskInfo.isSystem()) {
				systemtaskInfos.add(taskInfo);
			} else {
				usertaskInfos.add(taskInfo);
			}
		}
	}
	
	/**
	 * 得到可用内存的总大小
	 */
	public static long getAvailMem(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}

	/**
	 * 得到总内存大小
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static long getTotalMem(Context context) {
		long totalMem = 0;
		int sysVersion = VERSION.SDK_INT; // 得到当前系统的版本号
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
	 * 将大小格式化
	 * @param context
	 * @param byteSize
	 * @return
	 */
	public static String formatSize(Context context, long byteSize) {
		return Formatter.formatFileSize(context, byteSize);
	}

	/**
	 * 杀死指定进程
	 */
	public static void killProcess(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(packageName);
	}
	
	/**
	 * 得到运行的进程数
	 * @param context
	 * @return
	 */
	public static int getProcessSize(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		
		return processInfos.size();
	}

	/**
	 * 杀死所有进程
	 * @param context
	 */
	public static void killAllProcess(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		PackageManager pm = context.getPackageManager();

		for (RunningAppProcessInfo processInfo : processInfos) {
			// 包名
			String packageName = processInfo.processName;
			//过滤当前应用
			if(packageName.equals(context.getPackageName())) {
				continue;//当前应用的进程信息不要保存到集合中
			}
			
			killProcess(context, packageName);
		}
	}
	
	/**
	 * 得到应用的所有流量信息
	*/
	public static List<TrafficInfo> getAllTrafficInfos(Context context) {

		List<TrafficInfo> list = new ArrayList<TrafficInfo>();
		PackageManager pm = context.getPackageManager();
	     //安装的所有应用(包含没有主界面的)
		List<ApplicationInfo> infos = pm.getInstalledApplications(0); 
		for(ApplicationInfo info : infos) {
			TrafficInfo trafficInfo = new TrafficInfo();
			//appName
			String appName = info.loadLabel(pm).toString();
			trafficInfo.setName(appName);
			//icon
			Drawable icon = info.loadIcon(pm);
			trafficInfo.setIcon(icon);
			
			int uid = info.uid;   //userID
			//inSize 下载流量
			long inSize = TrafficStats.getUidRxBytes(uid); //receive
			trafficInfo.setInSize(inSize);
			//outSize 上传流量
			long outSize = TrafficStats.getUidTxBytes(uid);
			trafficInfo.setOutSize(outSize);
			list.add(trafficInfo);
		}
		return list;
	}

}
