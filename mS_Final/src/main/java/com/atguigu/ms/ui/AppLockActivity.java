package com.atguigu.ms.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.AppInfo;
import com.atguigu.ms.dao.AppLockDao;
import com.atguigu.ms.ui.AppLockActivity.AppLockAdapter.ViewHolder;
import com.atguigu.ms.util.MSUtils;

/**
 * 程序锁界面
 * @author 张晓飞
 *
 */
public class AppLockActivity extends Activity implements OnItemClickListener {

	protected static final int WHAT_SHOW_APPS = 1;
	private TextView tv_title;
	private ListView lv_app_lock;
	private LinearLayout ll_app_lock_progress;
	private AppLockAdapter adapter;
	private List<AppInfo> data = new ArrayList<AppInfo>();
	private AppLockDao appLockDao;
	private List<String> allLockPackageNames;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_SHOW_APPS:
				//3. 更新界面(主线程)
				ll_app_lock_progress.setVisibility(View.GONE);
				lv_app_lock.setAdapter(adapter);
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		
		init();
	}

	private void init() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		lv_app_lock = (ListView) findViewById(R.id.lv_app_lock);
		ll_app_lock_progress = (LinearLayout) findViewById(R.id.ll_app_lock_progress);
		
		tv_title.setText("程序锁");
		adapter = new AppLockAdapter();
		
		appLockDao = new AppLockDao(this);
		//得到所有需要锁定应用的包名的集合
		allLockPackageNames = appLockDao.getAllLocks();
		//设置listView的item点击事件
		lv_app_lock.setOnItemClickListener(this);
		
		
		//获取信息集合数据并显示(异步操作)
		
		//1. 显示提示视图(主线程)
		ll_app_lock_progress.setVisibility(View.VISIBLE);
		
		//2. 启动分线程干活
		new Thread(){
			public void run() {
				//在分线程, 读取所有应用信息的列表
				Map<Boolean, List<AppInfo>> map = MSUtils.getAllAppInfos(getApplicationContext());
				//添加用户应用的集合
				data.addAll(map.get(false));
				//添加t系统应用的集合
				data.addAll(map.get(true));
				
				//通知主线程显示列表(通过handler发消息)
				handler.sendEmptyMessage(WHAT_SHOW_APPS);
			}
		}.start();
	}
	
	class AppLockAdapter extends BaseAdapter {

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
			
			ViewHolder holder = null;
			if(convertView==null) {
				holder = new ViewHolder();
				convertView = View.inflate(AppLockActivity.this, R.layout.item_app_lock, null);
				holder.iconIV = (ImageView) convertView.findViewById(R.id.iv_app_lock_icon);
				holder.lockIV = (ImageView) convertView.findViewById(R.id.iv_app_lock);
				holder.nameTV = (TextView) convertView.findViewById(R.id.tv_app_lock_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			AppInfo appInfo = data.get(position);
			holder.iconIV.setImageDrawable(appInfo.getIcon());
			holder.nameTV.setText(appInfo.getAppName());
			
			//设置lock/unlock的图标
			//判断appInfo中的包名是否在allLockPackageNames中
			if(allLockPackageNames.contains(appInfo.getPackageName())) {//锁定
				holder.lockIV.setImageResource(R.drawable.lock);
				//保存锁定的标识
				holder.lockIV.setTag(true);
			} else {
				holder.lockIV.setImageResource(R.drawable.unlock);
				//保存解锁的标识
				holder.lockIV.setTag(false);
			}
			
			
			
			return convertView;
		}
		
		class ViewHolder {
			public ImageView iconIV;
			public ImageView lockIV;
			public TextView nameTV;
		}
	}

	//锁定或解锁一个应用
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		String packageName = data.get(position).getPackageName();
		//不要锁定自己
		if(packageName.equals(getPackageName())) {
			MSUtils.showMsg(getApplicationContext(), "不能锁定自己啊!!!");
			return;
		}
		//显示动画
		Animation animation = new TranslateAnimation(0, 50, 0, 0);
		animation.setDuration(500);
		view.startAnimation(animation );
		
		ViewHolder holder = (ViewHolder) view.getTag();
		boolean lock = (Boolean) holder.lockIV.getTag();
		//如果当前是锁定的, 就解锁(更新成解锁的图标, 删除对应的表数据)
		if(lock) {
			holder.lockIV.setImageResource(R.drawable.unlock);
			holder.lockIV.setTag(false);
			//appLockDao.delete(packageName);
			//通过resolver调用provider去删除
			Uri uri = Uri.parse("content://com.atguigu.ms.provider.applockprovider/lock_app/");
			getContentResolver().delete(uri , "package_name=?", new String[]{packageName});
			allLockPackageNames.remove(packageName);
		} else {
			//如果当前是解锁的, 就锁定(更新成锁定的图标, 添加对应的表数据)
			holder.lockIV.setImageResource(R.drawable.lock);
			holder.lockIV.setTag(true);
			//appLockDao.add(packageName);
			//通过resolver调用provider去插入
			Uri uri = Uri.parse("content://com.atguigu.ms.provider.applockprovider/lock_app/");
			ContentValues values = new ContentValues();
			values.put("package_name",packageName);
			getContentResolver().insert(uri, values );
			
			allLockPackageNames.add(packageName);
		}
	}
}
