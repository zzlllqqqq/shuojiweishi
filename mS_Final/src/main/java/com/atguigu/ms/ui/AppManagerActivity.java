package com.atguigu.ms.ui;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.AppInfo;
import com.atguigu.ms.util.MSUtils;

/**
 * 软件管理的界面
 * @author 张晓飞
 *
 */
public class AppManagerActivity extends Activity implements OnItemClickListener, OnClickListener {

	private TextView tv_title;
	private TextView tv_app_count;
	private ListView lv_apps;
	private LinearLayout ll_app_loading;
	private List<AppInfo> systemInfos;
	private List<AppInfo> userInfos;
	private AppAdapter adapter;
	private OnScrollListener onScrollListener = new OnScrollListener() {
		
		/**
		 * 滚动状态发生改变时调用 
		 * SCROLL_STATE_IDLE : 没有滚动: 滚动之前或停止滚动
		 * SCROLL_STATE_TOUCH_SCROLL : 手动按着滚动
		 * SCROLL_STATE_FLING : 快速滚动(手指迅速拖动后离开)
		 */
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			//一旦开始滚动, 移除显示Pw
			if(scrollState==OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {//开始滚动
				if(pw!=null && pw.isShowing()) {
					pw.dismiss();
				}
			}
		}
		
		/**
		 * 滚动-->move
		 * firstVisibleItem : 第一个可见的item的下标
		 */
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			
			if(userInfos==null || systemInfos==null)
				return;
			
			if(firstVisibleItem>=userInfos.size()+1) {
				tv_app_count.setText("系统应用: "+systemInfos.size());
			} else if(firstVisibleItem<=userInfos.size()) {
				tv_app_count.setText("用户应用: "+userInfos.size());
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		
		init();
	}


	private void init() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_app_count = (TextView) findViewById(R.id.tv_app_count);
		lv_apps = (ListView) findViewById(R.id.lv_apps);
		ll_app_loading = (LinearLayout) findViewById(R.id.ll_app_loading);
		
		adapter = new AppAdapter();
		tv_title.setText("软件管理");
		
		//给listVIew添加滚动的监听
		lv_apps.setOnScrollListener(onScrollListener );
		
		//给ListVIew设置item的点击监听
		lv_apps.setOnItemClickListener(this);
		
		
		//启动异步任务读取数据显示
		new AsyncTask<Void, Void, Void>() {

			protected void onPreExecute() {
				
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				//读取应用信息集合
				Map<Boolean, List<AppInfo>> map = MSUtils.getAllAppInfos(getApplicationContext());
				systemInfos = map.get(true);
				userInfos = map.get(false);
				
				return null;
			}
			
			protected void onPostExecute(Void result) {
				//显示用户应用的个数
				tv_app_count.setText("用户应用: "+userInfos.size());
				//显示列表
				lv_apps.setAdapter(adapter);
				//隐藏加载进度
				ll_app_loading.setVisibility(View.GONE);
			}
		}.execute();
		
		//注册监视应用卸载的receiver
		receiver = new UninstallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(receiver, filter );
	}
	
	private UninstallReceiver receiver;
	/**
	 * 接收应用卸载广播的receiver
	 * @author 张晓飞
	 *
	 */
	class UninstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {//一旦有应用被卸载, 此方法就会调用 
			//取出被卸载应用的包名
			String dataString = intent.getDataString();//package:com.atguigu.l04_datastorage
			String packageName = dataString.substring(dataString.indexOf(":")+1);
			//移除userInfos中对应的appInfo对象
			//根据packageName来重写hashCode()和equals()
			userInfos.remove(new AppInfo(packageName, null, null, false));
			//更新界面
			adapter.notifyDataSetChanged();
		}
	}
	
	class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {//用户应用个数+系统应用个数+2
			return userInfos.size()+systemInfos.size()+2;
		}

		@Override
		public Object getItem(int position) {
			if(position==0) {
				return userInfos.size();
			} else if(position>=1 && position<=userInfos.size()) {
				return userInfos.get(position-1);
			} else if(position==userInfos.size()+1) {
				return systemInfos.size();
			} else {
				return systemInfos.get(position-userInfos.size()-2);
			}
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//返回TextView
			if(position==0) {
				TextView textView = (TextView) View.inflate(AppManagerActivity.this, R.layout.app_count_view, null);
				textView.setText("用户应用: "+userInfos.size());
				return textView;
			} else if(position==userInfos.size()+1) {
				TextView textView = (TextView) View.inflate(AppManagerActivity.this, R.layout.app_count_view, null);
				textView.setText("系统应用: "+systemInfos.size());
				return textView;
			}
			//返回LinearLayout
			ViewHolder holder = null;
			if(convertView==null || convertView instanceof TextView) {//没有复用或复用的TextView
				holder = new ViewHolder();
				convertView = View.inflate(AppManagerActivity.this, R.layout.item_app, null);
				holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_app_icon);
				holder.textView = (TextView) convertView.findViewById(R.id.tv_item_app_name);
				convertView.setTag(holder);
			} else {
				//convertView是系统交给准备让你复用的视图, 不一定是LinearLayout, 也可能是TextView
				holder = (ViewHolder) convertView.getTag();
			}
			
			//取当前行的数据
			AppInfo appInfo = (AppInfo) getItem(position);
			//设置数据
			holder.imageView.setImageDrawable(appInfo.getIcon());
			holder.textView.setText(appInfo.getAppName());
			
			return convertView;
		}
		
		class ViewHolder {
			public ImageView imageView;
			public TextView textView;
		}
		
	}

	private PopupWindow pw; //只需要创建一次
	private View contentView;
	private int position = -1;//点击item的下标
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//如果点击的是应用个数的文本, 不需要显示
		if(view instanceof TextView)
			return;
		
		//保存点击的下标
		this.position = position;
		
		if(pw==null) {
			//创建pw对象
			contentView = View.inflate(AppManagerActivity.this, R.layout.pw_view, null);
			contentView.findViewById(R.id.ll_pw_uninstall).setOnClickListener(this);
			contentView.findViewById(R.id.ll_pw_run).setOnClickListener(this);
			contentView.findViewById(R.id.ll_pw_share).setOnClickListener(this);
			pw = new PopupWindow(contentView, view.getWidth()-80, view.getHeight());
			pw.setBackgroundDrawable(new BitmapDrawable());
		}
		//如果已经显示, 移除pw
		if(pw.isShowing()) {
			pw.dismiss();
		}
		//显示在当前行
		pw.showAsDropDown(view, 40, -view.getHeight());
		
		//显示一个动画
		Animation animation = new ScaleAnimation(0, 1, 0, 1); //以左顶点为中心, 不断放大
		animation.setDuration(500);
		contentView.startAnimation(animation);
	}


	@Override
	public void onClick(View v) {
		
		//得到当点击行的信息对象
		AppInfo appInfo = (AppInfo)adapter.getItem(position);
		
		
		switch (v.getId()) {
		case R.id.ll_pw_uninstall:
			//MSUtils.showMsg(this, "卸载 "+appInfo.getAppName());
			
			pw.dismiss();
			if(appInfo.isSystem()) {
				MSUtils.showMsg(this, "不能卸载系统应用");
			} else if(appInfo.getPackageName().equals(getPackageName())) {
				MSUtils.showMsg(this, "不能卸载本应用");
			} else {
				Intent intent = new Intent(Intent.ACTION_DELETE);
				intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
				startActivity(intent);
			}
			break;
		case R.id.ll_pw_run:
			//MSUtils.showMsg(this, "运行 "+appInfo.getAppName());
			pw.dismiss();
			startApp(appInfo.getPackageName());
			break;
		case R.id.ll_pw_share:
			//MSUtils.showMsg(this, "分享 "+appInfo.getAppName());
			pw.dismiss();
			shareApp(appInfo.getAppName());
			break;

		default:
			break;
		}
	}
	
	/**
	 * 分享
	 * @param appName
	 */
	private void shareApp(String appName) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");// 纯文本
		//intent.putExtra(Intent.EXTRA_SUBJECT, "应用分享");
		intent.putExtra(Intent.EXTRA_TEXT, "分享一个不错的应用: " + appName); // 内容
		startActivity(intent);
	}
	
	/**
	 * 启动
	 * @param packageName
	 */
	private void startApp(String packageName) {
		PackageManager packageManager = getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(packageName);
		if (intent == null) {
			MSUtils.showMsg(this, "此应用无法启动");
		} else {
			startActivity(intent);
		}
	}


}
