package com.example.app3_listview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapter.base.CommonBaseAdapter;
import com.example.adapter.base.ViewHolder;
import com.example.adapter.bean.AppInfo;

public class MainActivity extends Activity {

	private ListView lv_main_apps;
	private MyAdapter adapter;
	private List<AppInfo> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		lv_main_apps = (ListView) findViewById(R.id.lv_main_apps);
		
		
		//得到数据
		data = getAllAppInfos();
		
		//创建adapter
		adapter = new MyAdapter(this, data, R.layout.item_app);
		
		//设置adapter显示
		lv_main_apps.setAdapter(adapter);
		
		/*
			lv_main_apps.setAdapter(new CommonBaseAdapter<AppInfo>(this, data, R.layout.item_app) {
	
				@Override
				public void convert(ViewHolder holder, int position) {
					AppInfo appInfo = data.get(position);
					holder.setText(R.id.tv_item_name, appInfo.getAppName())
						.setImageDrawable(R.id.iv_item_icon, appInfo.getIcon());
				}
			});
		*/
		
		//设置每项(item)的点击监听
		lv_main_apps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {//position 点击的下标
				//得到数据
				AppInfo appInfo = data.get(position);
				String appName = appInfo.getAppName();
				//显示
				Toast.makeText(MainActivity.this, appName, 0).show();
			}
		});
	}

	/*
	 * 得到手机中所有应用信息的列表 AppInfo Drawable icon String appName String packageName
	 */
	protected List<AppInfo> getAllAppInfos() {

		List<AppInfo> list = new ArrayList<AppInfo>();
		// 得到应用的packgeManager
		PackageManager packageManager = getPackageManager();
		// 创建一个主界面的intent
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 得到包含应用信息的列表
		List<ResolveInfo> ResolveInfos = packageManager.queryIntentActivities(
				intent, 0);
		// 遍历
		for (ResolveInfo ri : ResolveInfos) {
			// 得到包名
			String packageName = ri.activityInfo.packageName;
			// 得到图标
			Drawable icon = ri.loadIcon(packageManager);
			// 得到应用名称
			String appName = ri.loadLabel(packageManager).toString();
			// 封装应用信息对象
			AppInfo appInfo = new AppInfo(icon, appName, packageName);
			// 添加到list
			list.add(appInfo);
		}
		return list;
	}
	
	/**
	 * 继承自CommonBaseAdapter的adapter类
	 *
	 */
	class MyAdapter extends CommonBaseAdapter<AppInfo> {
		public MyAdapter(Context context, List<AppInfo> data, int layoutId) {
			super(context, data, layoutId);
		}
		@Override
		public void convert(ViewHolder holder, int position) {
			//得到当前行的数据对象
			AppInfo appInfo = data.get(position);
			//设置数据
			holder.setText(R.id.tv_item_name, appInfo.getAppName())
				  .setImageDrawable(R.id.iv_item_icon, appInfo.getIcon());
		}
	}

	/*class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			//1. 得到view
			if(convertView==null) {
				convertView = View.inflate(MainActivity.this, R.layout.item_app, null);
			}
			
			//2. 得到数据
			AppInfo appInfo = data.get(position);
			
			//3. 显示数据
			ImageView iv_item_icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
			TextView tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
			iv_item_icon.setImageDrawable(appInfo.getIcon());
			tv_item_name.setText(appInfo.getAppName());
			
			return convertView;
		}

	}*/
}
