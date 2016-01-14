package com.atguigu.ms.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.dao.VirusDao;
import com.atguigu.ms.util.MSUtils;

/**
 * 病毒查杀的界面
 * @author 张晓飞
 *
 */
public class AntiVirusActivity extends Activity {

	private ImageView iv_antivirus_scanning;
	private TextView tv_antivirus_status;
	private ProgressBar pb_antivirus_progress;
	private LinearLayout ll_antivirus_container;
	private PackageManager pm;
	private List<VirusInfo> virusInfos = new ArrayList<VirusInfo>();//保存病毒应用信息的集合
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		
		init();
	}


	private void init() {
		iv_antivirus_scanning = (ImageView) findViewById(R.id.iv_antivirus_scanning);
		tv_antivirus_status = (TextView) findViewById(R.id.tv_antivirus_status);
		pb_antivirus_progress = (ProgressBar) findViewById(R.id.pb_antivirus_progress);
		ll_antivirus_container = (LinearLayout) findViewById(R.id.ll_antivirus_container);
		pm = getPackageManager();
		//显示扫描动画
		showScanAnimation();
		//开启异步扫描的工作
		startScanTask();
	}


	/**
	 * 开启异步扫描的工作
	 */
	private void startScanTask() {
		new AsyncTask<Void, VirusInfo, Void>() {

			protected void onPreExecute() {
				tv_antivirus_status.setText("准备开始扫描...");
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				//得到所有安装包应用信息集合
				List<PackageInfo> infos = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
				//设置进度的最大值
				pb_antivirus_progress.setMax(infos.size());
				
				for(PackageInfo info : infos) {
					VirusInfo virusInfo = new VirusInfo();
					String packageName = info.packageName;
					virusInfo.packageName = packageName;
					String appName = info.applicationInfo.loadLabel(pm).toString();
					virusInfo.appName = appName;
					String sigture = info.signatures[0].toCharsString();//得到签名字符串
					//MD5加密
					String md5 = MSUtils.md5(sigture);
					if("com.atguigu.quickindex2".equals(packageName)) {
						Log.i("TAG", md5);
					}
					
					//判断病毒库表中是否存在对应的记录, 如果存在说明是病毒应用
					boolean virus = VirusDao.isVirus(getApplicationContext(), md5);
					virusInfo.isVirus = virus;
					
					//如果当前是病毒应用, 保存起来
					if(virus) {
						virusInfos.add(virusInfo);
					}

					//发布进程(显示扫描进度)
					publishProgress(virusInfo);
					
					//休息一会
					SystemClock.sleep(40);
				}
				
				return null;
			}
			
			protected void onProgressUpdate(VirusInfo[] values) {
				VirusInfo info = values[0];
				//显示相关的信息
					//正在扫描xxx
				tv_antivirus_status.setText("正在查杀"+info.appName);
					//更新进度
				pb_antivirus_progress.incrementProgressBy(1);
					//显示扫描应用信息(是否是病毒)
				TextView textView = new TextView(AntiVirusActivity.this);
				if(info.isVirus) {
					textView.setText("发现病毒: "+info.appName);
					textView.setTextColor(Color.RED);
				} else {
					textView.setText("扫描安全: "+info.appName);
					textView.setTextColor(Color.BLACK);
				}
				ll_antivirus_container.addView(textView, 0);
			}
			
			protected void onPostExecute(Void result) {
				//根据扫描结果进行更新界面
					//动画停止
					iv_antivirus_scanning.clearAnimation();
					//进度条要隐藏
					pb_antivirus_progress.setVisibility(View.GONE);
					//扫描文本显示扫描结果
					tv_antivirus_status.setText("扫描完成, 发现"+virusInfos.size()+"个病毒应用");
				//如果有病毒应用, 提示卸载AlertDialog
					if(virusInfos.size()>0) {
						showUninstallDialog();
					}
			}
		}.execute();
	}

	/**
	 * 显示提示卸载病毒应用的dialog
	 */
	private void showUninstallDialog() {
		new AlertDialog.Builder(this)
			.setTitle("卸载病毒应用")
			.setMessage("是否确定卸载"+virusInfos.size()+"个病毒应用")
			.setPositiveButton("卸载", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//卸载应用
					for(VirusInfo info : virusInfos) {
						//启动卸载界面
						Intent intent = new Intent(Intent.ACTION_DELETE);
						intent.setData(Uri.parse("package:" + info.packageName));
						startActivity(intent);
					}
				}
			})
			.setNegativeButton("取消", null)
			.show();
	}


	class VirusInfo {
		public String packageName;
		private String appName;
		private boolean isVirus;//标识是否是一个病毒应用
	}

	/**
	 * 显示扫描动画
	 * 旋转动画: iv_antivirus_scanning
	 */
	private void showScanAnimation() {
		
		//创建旋转动画对象, 并设置数据
		RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(1000);
		animation.setRepeatCount(Integer.MAX_VALUE);
		animation.setInterpolator(new LinearInterpolator());//匀速变化
		//启动动画
		iv_antivirus_scanning.startAnimation(animation);
	}
}
