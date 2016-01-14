package com.atguigu.shoujiweishi.service;

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
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.atguigu.shoujiweishi.R;
import com.atguigu.shoujiweishi.receiver.ProcessWidget;
import com.atguigu.shoujiweishi.util.MSUtils;

/**
 * 更新widget的Service类
 */
public class UpdateWidgetService extends Service {
    private AppWidgetManager am;
    private ComponentName componentName;//componentName  组件名称   是在进行部件更新时候用到的
    private RemoteViews remoteViews;//远程视图
    private ScreenReceiver screenReceiver;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 1) {
                Log.i("TAG", "更新widget");
                updataWidget();
                //再发消息, 实现不断更新，结合下面handler发送的消息实现循环发送消息
                handler.sendEmptyMessageDelayed(1, 2000);
            }
        }
};
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //得到am
        am = AppWidgetManager.getInstance(this);
        componentName = new ComponentName(this, ProcessWidget.class);//指向要显示的widget
        Log.i("TAG", "&&&&&&&&   组件名称：" + componentName + "     ********");
        remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget_view);
        //设置监听
        Intent clickIntent = new Intent(this, UpdateWidgetService.class);//目标就是当前的service
        clickIntent.putExtra("action", "clear");
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, clickIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_widget_clear, pendingIntent);
        //立即更新
        updataWidget();

        handler.sendEmptyMessageDelayed(1, 2000);

        //注册广播监听开屏/锁屏
        screenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);
    }

    class ScreenReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Intent.ACTION_SCREEN_ON.equals(action)) {
                //发消息
                handler.removeMessages(1);
                handler.sendEmptyMessage(1);
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                //停止更新widget
                handler.removeMessages(1);
            }
        }
    }
    
    private void updataWidget() {
        //更新文本
        remoteViews.setTextViewText(R.id.tv_widget_process_count, "当前进程数："
                + MSUtils.getProcessCount(this));
        remoteViews.setTextViewText(R.id.tv_widget_process_memory, "可用内存："
                + MSUtils.formatSize(this, MSUtils.getAvailMem(this)));
        //更新widget
        am.updateAppWidget(componentName, remoteViews);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null) {
            String action = intent.getStringExtra("action");
            //如果发生点击那么在这里操作
            if("clear".equals(action)) {
                MSUtils.killProcess(this);
                updataWidget();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(screenReceiver);
    }
}
