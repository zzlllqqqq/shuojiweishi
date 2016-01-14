package com.atguigu.ms.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.UpdateInfo;
import com.atguigu.ms.net.APIClient;
import com.atguigu.ms.util.MSUtils;
import com.atguigu.ms.util.SpUtils;

/**
 * 欢迎界面
 * 
 * @author 张晓飞
 *
 */
public class WelcomeActivity extends Activity {

	protected static final int WHAT_REQUEST_UPDATE_SUCCESS = 1;//得到更新信息成功
	protected static final int WHAT_REQUEST_UPDATE_ERROR = 2;//得到更新信息失败
	protected static final int WHAT_DOWNLOAD_SUCCESS = 3;//下载apk成功
	protected static final int WHAT_DOWNLOAD_ERROR = 4;//下载apk失败
	private static final int WHAT_START_MAIN = 5;//进入主界面
	
	private LinearLayout ll_welcome;
	private TextView tv_welcome_version;
	private String currentVersion;
	private UpdateInfo info; //最新版本信息对象
	private long startTime;//开始的时间
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_REQUEST_UPDATE_SUCCESS:
				if(currentVersion.equals(info.getVersion())) {//当前就是最新版本,直接进入主界面
					MSUtils.showMsg(getApplicationContext(), "当前是最新版本");
					toMain();
				} else {//有更新的版本
					//显示下载的dialog
					showDownloadDialog();
				}
				break;
			case WHAT_REQUEST_UPDATE_ERROR://得到更新信息失败, 提示并进入主界面
				Toast.makeText(getApplicationContext(), "获取最新版本信息失败", 0).show();
				toMain();
				break;
			case WHAT_DOWNLOAD_SUCCESS:
				//安装apk
				installApk();
				break;
			case WHAT_DOWNLOAD_ERROR://得到更新信息失败, 提示并进入主界面
				Toast.makeText(getApplicationContext(), "下载apk失败", 0).show();
				toMain();
				break;
			case WHAT_START_MAIN:
				finish();
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 去掉窗口标题
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏顶部的状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_welcome);
		
		//得到当前时间
		startTime = System.currentTimeMillis();
		
		//初始化视图
		ll_welcome = (LinearLayout) findViewById(R.id.ll_welcome);
		tv_welcome_version = (TextView) findViewById(R.id.tv_welcome_version);
		
		//1. 显示动画
		showAnimation();
		
		//2. 显示当前版本号
		currentVersion = getVersion();
		tv_welcome_version.setText("版本号: "+currentVersion);
		
		//3. 拷贝assets下的所有db文件到手机内部的files
		copyDatabases();
		
		//4. 版本更新检查
		checkVersion();
		
		//5. 生成应用桌面快捷方式
		makeShortcut();
	}

	/**
	 * 生成应用桌面快捷方式
	 */
	private void makeShortcut() {
		
		boolean shortcut = SpUtils.getInstance(this).getBoolean(SpUtils.SHORT_CUT, false);
		
		if(!shortcut) {//说明没有生成过
			Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			//图标
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.logo2));
			//名称
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "快速访问");
			//点击启动Activity的intent
			Intent clickIntent = new Intent("com.atguigu.ms.MainAction");
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, clickIntent);
			//发广播
			sendBroadcast(intent);
			//保存
			SpUtils.getInstance(this).save(SpUtils.SHORT_CUT, true);
		}
		
	}

	/**
	 * 安装apk
	 */
	private void installApk() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * 显示下载的dialog
	 */
	private void showDownloadDialog() {
		new AlertDialog.Builder(this)
			.setTitle("下载最新版本")
			.setMessage(info.getDesc())
			.setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//启动分线程下载apk
					downApk();
				}
			})
			.setNegativeButton("暂不下载", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					toMain();
				}
			})
			.show();
	}

	
	private ProgressDialog pd;
	private File apkFile;
	
	/**
	 * 下载最新的APK
	 * ProgressDailog
	 * apkFile
	 */
	private void downApk() {
		Toast.makeText(this, "下载最新的APK", 0).show();
		
		//准备pd
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		//准备apkFile   // /sdcard/udpate.apk
		File sdFile = Environment.getExternalStorageDirectory();
		apkFile = new File(sdFile, "update.apk");
		
		//启动分线程下载Apk
		new Thread(){
			public void run() {
				//下载
				try {
					APIClient.downloadAPK(getApplicationContext(), pd, apkFile, info.getApkUrl());
					//下载成功
					handler.sendEmptyMessage(WHAT_DOWNLOAD_SUCCESS);
				} catch (Exception e) {//下载失败
					e.printStackTrace();
					handler.sendEmptyMessage(WHAT_DOWNLOAD_ERROR);
				} finally {
					//移除dialog
					pd.dismiss();
				}
			}
		}.start();
		
	}

	/**
	 * 拷贝assets下的所有db文件到手机内部的files
	 */
	private void copyDatabases() {
		new Thread(){
			public void run() {
				//复制三个数据库文件
				copyDatabase("address.db");
				copyDatabase("antivirus.db");
				copyDatabase("commonnum.db");
			}
		}.start();
	}

	/**
	 * 拷贝assets下的指定db文件到手机内部的files
	 *  /data/data/packageName/files/xxx.db
	 * @param string
	 */
	private void copyDatabase(String fileName) {
		
		try {
			//判断files是否存在此文件, 如果存在结束
			//得到File: /data/data/packageName/files/
			File filesDir = getFilesDir();
			//创建File对象: /data/data/packageName/files/xxx.db
			File file = new File(filesDir, fileName);
			if(file.exists()) {//存在退出
				Log.i("TAG", "文件已存在, 不需要拷贝");
				return;
			}//不存在, 需要拷贝
			
			//得到文件的输入流
			AssetManager assetManager = getAssets();
			InputStream is = assetManager.open(fileName);
			
			//得到文件的输出流
			FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
			//边读边写
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len=is.read(buffer))>0) {
				fos.write(buffer, 0, len);
			}
			//关闭流
			fos.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 当前版本号
	 * @return
	 */
	private String getVersion() {
		String version = "未知版本";
		PackageManager manager = getPackageManager();
		try {
			PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			//e.printStackTrace(); //如果找不到对应的应用包信息, 就返回"未知版本"
		}
		
		return version;
	}

	/**
	 * 版本更新检查
	 */
	private void checkVersion() {
		//检查手机是否联网
		boolean connected = isConnected();
		if(!connected) {
			Toast.makeText(this, "没有联网...", 0).show();
			//进入主界面
			toMain();
		} else {//联上了
			//Toast.makeText(this, "连接上了", 0).show();
			//启动分线程请求服务器得到最新版本的信息并封装为UpdateInfo对象
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						//请求服务器得到最新版本的信息并封装为UpdateInfo对象
						info = APIClient.getUpdateInfo();
						handler.sendEmptyMessage(WHAT_REQUEST_UPDATE_SUCCESS);
					} catch (Exception e) {//请求失败
						//e.printStackTrace();
						handler.sendEmptyMessage(WHAT_REQUEST_UPDATE_ERROR);
					}
				}
			}).start();
			
		}
		
		
	}

	/**
	 * 进入主界面
	 */
	private void toMain() {
		
		long time = System.currentTimeMillis();
		//得到需要延迟的时间
		int delayTime = (int) (2000-(time-startTime));
		
		if(delayTime<0) {
			delayTime = 0;
		}
		handler.sendEmptyMessageDelayed(WHAT_START_MAIN, delayTime);
	}

	/**
	 * 判断手机是否联网
	 * ConnectivityManager
	 * @return
	 */
	private boolean isConnected() {
		boolean connected = false;
		
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if(networkInfo!=null) {
			connected = networkInfo.isConnected();
		}
		
		return connected;
	}

	/**
	 * 显示动画: 
	 * 	透明度: 0--1 持续2s, 
	 * 	缩放: 0-->1 持续2s, 
	 *  旋转: 0-->360 持续2s
	 */
	private void showAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		//透明度
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(2000);
		//缩放
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(2000);
		//旋转: 0-->360 持续2s
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360, 
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(2000);
		//添加到集合中
		animationSet.addAnimation(rotateAnimation);
		animationSet.addAnimation(scaleAnimation);
		animationSet.addAnimation(alphaAnimation);
		
		//启动动画 
		ll_welcome.startAnimation(animationSet);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//移除所有未处理的消息
		handler.removeCallbacksAndMessages(null);
	}
	
}
