package com.atguigu.ms.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.TaskInfo;
import com.atguigu.ms.ui.TaskManagerActivity.TaskAdapter.ViewHolder;
import com.atguigu.ms.util.MSUtils;

/**
 * 进程管理界面
 * @author 张晓飞
 *
 */
public class TaskManagerActivity extends Activity implements OnItemClickListener {

	private TextView tv_task_count;
	private TextView tv_task_size;
	private ListView lv_tasks;
	private LinearLayout ll_task_loading;
	private TextView tv_task_user_count;
	private RelativeLayout rl_task_info;
	
	private List<TaskInfo> userTaskInfos = new ArrayList<TaskInfo>();
	private List<TaskInfo> systemTaskInfos = new ArrayList<TaskInfo>();
	private long availMemSize;
	private long totalMemSize;
	
	private TaskAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		
		init();
	}
	
	private void init() {
		rl_task_info = (RelativeLayout) findViewById(R.id.rl_task_info);
		tv_task_count = (TextView) findViewById(R.id.tv_task_count);
		tv_task_size = (TextView) findViewById(R.id.tv_task_size);
		lv_tasks = (ListView) findViewById(R.id.lv_tasks);
		lv_tasks.setOnItemClickListener(this);
		ll_task_loading = (LinearLayout) findViewById(R.id.ll_task_loading);
		tv_task_user_count = (TextView) findViewById(R.id.tv_task_user_count);
		
		adapter = new TaskAdapter();
		
		//启动异步任务加载数据并显示
		new AsyncTask<Void, Void, Void>() {
			
			protected void onPreExecute() {
				//隐藏一些视图
				rl_task_info.setVisibility(View.GONE);
				tv_task_user_count.setVisibility(View.GONE);
				//显示一些视图
				ll_task_loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				//获取数据
					//进程信息集合
					MSUtils.getAllTaskInfos(getApplicationContext(), systemTaskInfos, userTaskInfos);
					//可用内存
					availMemSize = MSUtils.getAvailMem(getApplicationContext());
					//总内存
					totalMemSize = MSUtils.getTotalMem(getApplicationContext());
				
				return null;
			}
			
			protected void onPostExecute(Void result) {
				//显示隐藏的视图
				rl_task_info.setVisibility(View.VISIBLE);
				tv_task_user_count.setVisibility(View.VISIBLE);
				//隐藏显示的提示视图
				ll_task_loading.setVisibility(View.GONE);
				//显示进程数据
				tv_task_count.setText("进程数: "+(userTaskInfos.size()+systemTaskInfos.size()));
				//显示可用内存和总内存
				tv_task_size.setText("剩余/总内存: "+MSUtils.formatSize(getApplicationContext(), availMemSize)
						+"/"+MSUtils.formatSize(getApplicationContext(), totalMemSize));
				
				//显示用户进程数
				tv_task_user_count.setText("用户进程: "+userTaskInfos.size());
				//显示列表
				lv_tasks.setAdapter(adapter);
			}
		}.execute();
		
		
		//设置listView的滚动监听
		lv_tasks.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(userTaskInfos==null)
					return;
				if(firstVisibleItem>=userTaskInfos.size()+1) {//显示系统进程数
					tv_task_user_count.setText("系统进程: "+systemTaskInfos.size());
				} else {//显示用户进程数
					tv_task_user_count.setText("用户进程: "+userTaskInfos.size());
				}
			}
		});
	}
	
	
	class TaskAdapter extends BaseAdapter {
		/**
		 * userInfos size=4
		 * systemInfos size=10
		 */
		@Override
		public int getCount() {  //4+10+2;
			return userTaskInfos.size()+systemTaskInfos.size()+2;
		}

		@Override
		public Object getItem(int position) {
			if(position==0) {
				return userTaskInfos.size();
			} else if(position>0 && position<=userTaskInfos.size()) {//1---4   0---3
				return userTaskInfos.get(position-1);
			} else if(position==userTaskInfos.size()+1) {//5
				return systemTaskInfos.size();
			} else {//6--15 0--9
				return systemTaskInfos.get(position-userTaskInfos.size()-2);
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
				TextView textView = (TextView) View.inflate(TaskManagerActivity.this, R.layout.app_count_view, null);
				textView.setText("用户进程: "+userTaskInfos.size());
				return textView;
			} else if(position==userTaskInfos.size()+1) {
				TextView textView = (TextView) View.inflate(TaskManagerActivity.this, R.layout.app_count_view, null);
				textView.setText("系统进程: "+systemTaskInfos.size());
				return textView;
			}
			//返回RelativeLayout
			ViewHolder holder = null;
			if(convertView==null || convertView instanceof TextView) {
				holder = new ViewHolder();
				convertView = View.inflate(TaskManagerActivity.this, R.layout.item_process_manger, null);
				holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_task_logo);
				holder.nameTV = (TextView) convertView.findViewById(R.id.tv_item_task_name);
				holder.sizeTV = (TextView) convertView.findViewById(R.id.tv_item_task_mem);
				holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_item_task_status);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			TaskInfo info = (TaskInfo) getItem(position);
			holder.imageView.setImageDrawable(info.getIcon());
			holder.nameTV.setText(info.getAppName());
			holder.sizeTV.setText(MSUtils.formatSize(getApplicationContext(), info.getMemInfoSize()));
			holder.checkBox.setChecked(info.isChecked());
			
			
			return convertView;
		}
		
		
		class ViewHolder {
			public ImageView imageView;
			public TextView nameTV;
			public TextView sizeTV;
			public CheckBox checkBox;
		}
		
	}

	/**
	 * 全选
	 * @param v
	 */
	public void selectAll(View v) {
		for(TaskInfo info :userTaskInfos) {
			info.setChecked(true);
		}
		for(TaskInfo info :systemTaskInfos) {
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 反选
	 * @param v
	 */
	public void reverseSelect(View v) {
		for(TaskInfo info :userTaskInfos) {
			info.setChecked(!info.isChecked());
		}
		for(TaskInfo info :systemTaskInfos) {
			info.setChecked(!info.isChecked());
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 一键清理
	 * @param v
	 */
	public void clearAll(View v) {
		//统计杀死的进程数
		int killSize = 0;
		//统计释放的内存
		long releaseMemSize = 0;
		//杀死选中的用户进程
		Iterator<TaskInfo> iterator = userTaskInfos.iterator();
		while(iterator.hasNext()) {
			TaskInfo taskInfo = iterator.next();
			if(taskInfo.isChecked()) {//如果标识是选中的,这个进程需要杀死
				//杀死进程
				MSUtils.killProcess(getApplicationContext(), taskInfo.getPackageName());
				//移除数据
				iterator.remove();
				//统计
				killSize++;
				releaseMemSize += taskInfo.getMemInfoSize();
			}
		}
		
		//杀死选中的系统进程
		iterator = systemTaskInfos.iterator();
		while(iterator.hasNext()) {
			TaskInfo taskInfo = iterator.next();
			if(taskInfo.isChecked()) {//如果标识是选中的,这个进程需要杀死
				//杀死进程
				MSUtils.killProcess(getApplicationContext(), taskInfo.getPackageName());
				//移除数据
				iterator.remove();
				//统计
				killSize++;
				releaseMemSize += taskInfo.getMemInfoSize();
			}
		}
		
		//更新可用内存大小
		availMemSize = availMemSize+releaseMemSize;
		
		//显示提示
		String msg = "杀死"+killSize+"进程, 释放"+MSUtils.formatSize(this, releaseMemSize)+"空间";
		MSUtils.showMsg(this, msg);
		//更新listView
		adapter.notifyDataSetChanged();
		//更新用户进程数
		tv_task_user_count.setText("用户进程: "+userTaskInfos.size());
		//更新总进程数
		tv_task_count.setText("进程数: "+userTaskInfos.size()+systemTaskInfos.size());
		//更新内存大小
		tv_task_size.setText("剩余/总内存: "+MSUtils.formatSize(getApplicationContext(), availMemSize)
				+"/"+MSUtils.formatSize(getApplicationContext(), totalMemSize));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//过滤LIstView中的2个文本行
		if(position==0 || position==userTaskInfos.size()+1) {
			return;
		}
		//得到当前行的taskInfo
		TaskInfo info = (TaskInfo) adapter.getItem(position);
		//找到CheckBox
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.checkBox.setChecked(!holder.checkBox.isChecked());
		//holder.checkBox.toggle(); //切换选中状态
		//保存checkbox的选中状态
		info.setChecked(holder.checkBox.isChecked());
	}
}
