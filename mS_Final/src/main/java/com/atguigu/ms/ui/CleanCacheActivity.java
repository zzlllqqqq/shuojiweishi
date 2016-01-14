package com.atguigu.ms.ui;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.util.MSUtils;

/**
 * 缓存清理界面
 * @author 张晓飞
 *
 */
public class CleanCacheActivity extends Activity {

	private TextView tv_cache_clean_status;
	private ProgressBar pb_cache_clean;
	private LinearLayout ll_cache_clean_contaner;
	private PackageManager pm;
	private static final int WHAT_SCANNING = 1;
	private long totalCacheSize = 0;//总缓存大小
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==WHAT_SCANNING) {
				
				final CacheInfo info = (CacheInfo) msg.obj;
				//更新进度文本
				tv_cache_clean_status.setText("扫描 "+info.appName);
				//更新进度
				pb_cache_clean.incrementProgressBy(1);
				//显示扫描的应用的信息
					//加载item布局
					View view = View.inflate(CleanCacheActivity.this, R.layout.item_cache_clean, null);
					//设置数据
					ImageView imageView = (ImageView) view.findViewById(R.id.iv_item_cache_icon);
					imageView.setImageDrawable(info.icon);
					TextView appNameTV = (TextView) view.findViewById(R.id.tv_item_cache_name);
					appNameTV.setText(info.appName);
					TextView sizeTV = (TextView) view.findViewById(R.id.tv_item_cache_size);
					sizeTV.setText(info.cacheSize);
					//设置点击监听
					view.findViewById(R.id.iv_item_cache_delete).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							//进入应用详情界面
							Intent intent = new Intent();
							intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							intent.setData(Uri.parse("package:" + info.packageName));
							startActivity(intent);
						}
					});
					
					
					//将view添加为ll_cache_clean_contaner的第一个子View
					ll_cache_clean_contaner.addView(view, 0);
				
				//当扫描完成, 更新界面
				if(pb_cache_clean.getProgress()==pb_cache_clean.getMax()) {
					pb_cache_clean.setVisibility(View.GONE);
					tv_cache_clean_status.setText("共扫描到"+pb_cache_clean.getMax()+"项缓存数据,总大小"+MSUtils.formatSize(getApplicationContext(), totalCacheSize));
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		
		init();
	}

	private void init() {
		tv_cache_clean_status = (TextView) findViewById(R.id.tv_cache_clean_status);
		pb_cache_clean = (ProgressBar) findViewById(R.id.pb_cache_clean);
		ll_cache_clean_contaner = (LinearLayout) findViewById(R.id.ll_cache_clean_contaner);
		pm = getPackageManager();
		//启动分线程开启扫描的工作
		new Thread(){
			public void run() {
				//得到安装的所有应用
				List<ApplicationInfo> applications = pm.getInstalledApplications(0);
				//设置进度条的最大进程
				pb_cache_clean.setMax(applications.size());
				//遍历
				for(ApplicationInfo info : applications) {
					
					SystemClock.sleep(20);
					
					final CacheInfo cacheInfo = new CacheInfo();
					cacheInfo.packageName = info.packageName;
					cacheInfo.appName = info.loadLabel(pm).toString();
					cacheInfo.icon = info.loadIcon(pm);
					
					//通过反射和AIDL来得到cachesize
					try {
						Method method = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
						method.invoke(pm, cacheInfo.packageName, new IPackageStatsObserver.Stub() {

							@Override
							public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
									throws RemoteException { //当获取包的大小信息完成后回调
								if(succeeded) {
									//统计总缓存大小
									totalCacheSize += pStats.cacheSize;
									//取缓存
									cacheInfo.cacheSize = MSUtils.formatSize(getApplicationContext(), pStats.cacheSize);
								} else {
									cacheInfo.cacheSize = "未知大小";
								}
								//通知更新界面
								Message msg = Message.obtain();
								msg.what = WHAT_SCANNING;
								msg.obj = cacheInfo;
								handler.sendMessage(msg);
							}
						});
					
					
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	/**
	 * 应用缓存的相关信息
	 * @author 张晓飞
	 *
	 */
	class CacheInfo {
		public String packageName;
		public String appName;
		public Drawable icon;
		public String cacheSize;//缓存大小
	}
}
