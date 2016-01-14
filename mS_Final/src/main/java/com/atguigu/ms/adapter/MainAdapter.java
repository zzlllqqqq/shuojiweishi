package com.atguigu.ms.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.util.SpUtils;

/**
 * 主界面的Adpater
 * @author 张晓飞
 *
 */
public class MainAdapter extends BaseAdapter {

	private Context context;
	private static final String[] NAMES = new String[] { 
		"手机防盗", "通讯卫士", "软件管理", "流量管理", "进程管理",
			"手机杀毒", "缓存清理", "高级工具", "设置中心" };

	private static final int[] ICONS = new int[] { R.drawable.widget01, 
			R.drawable.widget02,R.drawable.widget03, R.drawable.widget04, 
			R.drawable.widget05, R.drawable.widget06,R.drawable.widget07, 
			R.drawable.widget08, R.drawable.widget09 };
	//private SharedPreferences sp;

	public MainAdapter(Context context) {
		this.context = context;
		//sp = context.getSharedPreferences("ms", Context.MODE_PRIVATE);
	}
	@Override
	public int getCount() {
		return NAMES.length;
	}

	@Override
	public Object getItem(int position) {
		return NAMES[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//得到或创建holder对象
		ViewHolder holder = null;
		if(convertView==null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_main, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_icon);
			holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		//给Holder对象中的视图设置数据
		holder.imageView.setImageResource(ICONS[position]);
		holder.textView.setText(NAMES[position]);
		
		//读取sp中保存的lost_name, 如果有就显示此名称
		if(position==0) {
			//String savedName = sp.getString("lost_name", null);
			String savedName = SpUtils.getInstance(context).getString(SpUtils.LOST_NAME, null);
			if(savedName!=null) {//有, 显示
				holder.textView.setText(savedName);
			}
		}
		
		//返回view
		return convertView;
	}
	
	public static class ViewHolder {
		public ImageView imageView;
		public TextView textView;
	}

}
