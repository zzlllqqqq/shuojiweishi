package com.atguigu.ms.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.atguigu.ms.dao.AppLockDao;
import com.atguigu.ms.ui.LockScreenActivity;

/**
 * 程序锁服务
 * @author 张晓飞
 *
 */
/*
List<String> lockPackageNames;//在onCreate()中初始化
List<String> tempNolockPackageNames;//临时不需要解锁的应用包名的集合
new Thread(){
run(){
	while(flag) {//默认为true, 当service停止时变为false
		//1. 得到最顶部栈的应用包名

		//2. 判断是否在lockPackageNames中, 且不在
				tempNolockPackageNames中

		//3. 如果为true, 启动解锁的界面

		//4. 适当的休息休息
	}
}
}
 */
public class AppLockService extends Service {

	//所有需要锁定应用的包名的集合
	private List<String> lockPackageNames;
	//临时不需要锁定的应用包名的集合
	private List<String> tempNolockPackageNames = new ArrayList<String>();
	//标识是否一直监听着
	private boolean flag = true;
	
	private AppLockDao appLockDao;
	
	private ActivityManager am;
	private ContentObserver observer = new ContentObserver(null) {
		@Override
		public void onChange(boolean selfChange) {//当表数据变化, AppLockProvider发通知,此方法就会调用
			//查询是新的lock_app表记录列表
			lockPackageNames = appLockDao.getAllLocks();
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("TAG", "AppLockService onCreate()");
		
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		appLockDao = new AppLockDao(this);
		lockPackageNames = appLockDao.getAllLocks();
		
		//注册观察者
		Uri uri = Uri.parse("content://com.atguigu.ms.provider.applockprovider/lock_app");
		getContentResolver().registerContentObserver(uri, true, observer );
		
		//启动监视的线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(flag) {
					//1. 得到最顶部栈的应用包名
					String topPackageName = getTopPackageName();
					Log.i("TAG", "topPackageName="+topPackageName);

					//2. 判断是否在lockPackageNames中, 且不在tempNolockPackageNames中
					boolean lock = lockPackageNames.contains(topPackageName) 
							&& !tempNolockPackageNames.contains(topPackageName);
					//3. 如果为true, 启动解锁的界面
					if(lock) {
						showUnlockUI(topPackageName);
					}

					//4. 适当的休息休息
					SystemClock.sleep(100);
				}
			}
		}).start();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(intent!=null) {
			//从Intent中取出包名
			String packageName = intent.getStringExtra("PACKAGE_NAME");
			//如果有, 保存到tempNolockPackageNames
			if(packageName!=null) {
				tempNolockPackageNames.add(packageName);
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	/**
	 * 启动解锁的界面
	 */
	private void showUnlockUI(String packageName) {
		Intent intent = new Intent(this, LockScreenActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必须是此flag
		intent.putExtra("packagename", packageName);//携带包名数据
		startActivity(intent );
	}

	/**
	 * 得到最顶部栈的应用包名
	 * @return
	 */
	private String getTopPackageName() {
		return am.getRunningTasks(1).get(0).topActivity.getPackageName();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("TAG", "AppLockService onDestroy()");
		flag = false;//监听的线程就会停止工作
		//解注册观察者
		getContentResolver().unregisterContentObserver(observer);
	}
}
