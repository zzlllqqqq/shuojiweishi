package com.atguigu.ms.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.TrafficInfo;
import com.atguigu.ms.util.MSUtils;

/**
 * 流量管理界面
 * @author 张晓飞
 *
 */
public class TrafficManagerActivity extends Activity {

	protected static final int WHAT_SHOW = 1;
	private TextView tv_traffic_2g_3g;
	private TextView tv_traffic_wifi;
	private SlidingDrawer sd_traffic;
	private ListView lv_traffic;
	private List<TrafficInfo> data;
	private long traffic_2g_3g;
	private long traffic_wifi;
	private TrafficAdapter adapter;
	private ProgressDialog pd;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==WHAT_SHOW) {
				//移除提示pd
				pd.dismiss();
				
				//显示总流量 
				tv_traffic_2g_3g.setText("2G/3G流量: "+MSUtils.formatSize(getApplicationContext(), traffic_2g_3g));
				tv_traffic_wifi.setText("WIFI流量: "+MSUtils.formatSize(getApplicationContext(), traffic_wifi));
				//显示列表
				lv_traffic.setAdapter(adapter);
				
				//打开抽屉
				//sd_traffic.open();
				sd_traffic.animateOpen();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic_manager);
		
		init();
	}

	private void init() {
		tv_traffic_2g_3g = (TextView) findViewById(R.id.tv_traffic_2g_3g);		
		tv_traffic_wifi = (TextView) findViewById(R.id.tv_traffic_wifi);		
		sd_traffic = (SlidingDrawer) findViewById(R.id.sd_traffic);		
		lv_traffic = (ListView) findViewById(R.id.lv_traffic);		
		adapter = new TrafficAdapter();
		
		
		//显示提示视图
		pd = ProgressDialog.show(this, null, "正在读取中...");
		
		//启动分线程干活
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				SystemClock.sleep(1000);
				
				//在分线程中获取所 的流量 信息集合
				data = MSUtils.getAllTrafficInfos(getApplicationContext());
				
				/*
				 * 右边-左边
				 * >0 大数在左边
				 * <0 大数在右边(换位)
				 * ==0  相等
				 */
				
				//对data集合中的对象进行排序: 按流量
				Collections.sort(data, new Comparator<TrafficInfo>() {

					@Override
					public int compare(TrafficInfo lhs, TrafficInfo rhs) {
						long leftTraffix = lhs.getInSize()+lhs.getOutSize();
						long rightTraffix = rhs.getInSize()+rhs.getOutSize();
						
						return (int) (rightTraffix-leftTraffix);
					}
				});
				
				
				//得到手机总的下载流量和上传流量(2g/3g/wifi)
				long totalRxBytes = TrafficStats.getTotalRxBytes();
				long totalTxBytes = TrafficStats.getTotalTxBytes();
				//得到手机的下载流量和上传流量(2g/3g)
				long mobileRxBytes = TrafficStats.getMobileRxBytes();
				long mobileTxBytes = TrafficStats.getMobileTxBytes();
				
				//2g/3g的总流量 
				traffic_2g_3g = mobileRxBytes+mobileTxBytes;
				//wifi的总流量 
				traffic_wifi = totalRxBytes+totalTxBytes-traffic_2g_3g;
				
				//通知更新
				handler.sendEmptyMessage(WHAT_SHOW);
			}
		}).start();
	}
	
	class TrafficAdapter extends BaseAdapter {

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
				convertView = View.inflate(TrafficManagerActivity.this, R.layout.item_traffic_manager, null);
				holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_traffic_icon);
				holder.nameTV = (TextView) convertView.findViewById(R.id.tv_item_traffic_name);
				holder.downloadTV = (TextView) convertView.findViewById(R.id.tv_item_traffic_received);
				holder.uploadTV = (TextView) convertView.findViewById(R.id.tv_item_traffic_transmitted);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			TrafficInfo trafficInfo = data.get(position);
			holder.imageView.setImageDrawable(trafficInfo.getIcon());
			holder.nameTV.setText(trafficInfo.getName());
			holder.uploadTV.setText(MSUtils.formatSize(getApplicationContext(), trafficInfo.getInSize()));
			holder.downloadTV.setText(MSUtils.formatSize(getApplicationContext(), trafficInfo.getOutSize()));
			
			return convertView;
		}
		
		class ViewHolder {
			public ImageView imageView;
			public TextView nameTV;
			public TextView uploadTV;
			public TextView downloadTV;
		}
	}
}
