package com.atguigu.ms.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.atguigu.ms.R;
import com.atguigu.ms.receiver.MsWidgetProvider;
import com.atguigu.ms.util.MSUtils;

/**
 * 用来更新AppWidget的服务
 * @author 张晓飞
 *
 */
public class AppWidgetService extends Service {

	private static final int WHAT_UPDATE_WIDGET = 1;
	private AppWidgetManager am;
	private RemoteViews remoteViews;
	private ComponentName provider;
	private ScreeenReceiver receiver;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_UPDATE_WIDGET:
				Log.i("TAG", "handler updatewidget....");
				//更新widget
				updateWidget();
				//再发延迟消息
				sendEmptyMessageDelayed(WHAT_UPDATE_WIDGET, 2000);
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//创建am对象
		am = AppWidgetManager.getInstance(this); 
		
		provider = new ComponentName(this, MsWidgetProvider.class);
		
		//创建RemoteViews
		remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget_view);
		
		//给button添加监听
		Intent intent = new Intent(this, AppWidgetService.class);
		//添加标识清理的额外数据
		intent.putExtra("clear", true);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
		remoteViews.setOnClickPendingIntent(R.id.btn_widget_clear, pendingIntent);
	
		//更新widget
		updateWidget();
		
		//发送延迟2s的消息(更新widget)
		handler.sendEmptyMessageDelayed(WHAT_UPDATE_WIDGET, 2000);
		
		//注册广播
		receiver = new ScreeenReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);  //开屏
		filter.addAction(Intent.ACTION_SCREEN_OFF); //锁屏
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//解注册广播接收器
		unregisterReceiver(receiver);
	}



	/**
	 * 更新widget
	 */
	private void updateWidget() {
		//设置进程数
		int processSize = MSUtils.getProcessSize(this);
		remoteViews.setTextViewText(R.id.tv_widget_process_count, "当前进程数: "+processSize);
		//设置可用内存数
		String availMemSize = MSUtils.formatSize(this, MSUtils.getAvailMem(this));
		remoteViews.setTextViewText(R.id.tv_widget_process_memory, "可用内存: "+availMemSize);
		//更新widget
		am.updateAppWidget(provider, remoteViews);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		boolean clear = intent.getBooleanExtra("clear", false);
		Log.i("TAG", "onStartCommand clear="+clear);
		if(clear) {
			//清理进程
			MSUtils.killAllProcess(this);
			//更新widget
			updateWidget();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 监视锁屏和开屏的广播的receiver
	 * @author 张晓飞
	 *
	 */
	class ScreeenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(Intent.ACTION_SCREEN_ON.equals(action)) {//开屏
				handler.removeCallbacksAndMessages(null);//移除所有未处理的消息
				//发送更新widget消息
				handler.sendEmptyMessage(WHAT_UPDATE_WIDGET);
			} else {//锁屏
				//移除未处理的消息
				handler.removeCallbacksAndMessages(null);
			}
		}
	}
}
