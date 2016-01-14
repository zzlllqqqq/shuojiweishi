package com.atguigu.ms.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.atguigu.ms.service.AppWidgetService;

/**
 * 接收操作widget的广播的recever
 * @author 张晓飞
 *
 */
public class MsWidgetProvider extends AppWidgetProvider {

	//当使widget可操作后调用
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.i("TAG", "MsWidgetProvider onEnabled()");
		//启动服务, 更新widget
		context.startService(new Intent(context, AppWidgetService.class));
	}
	
	//当移除widget时调用 
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.i("TAG", "MsWidgetProvider onDeleted()");
		//停止服务
		context.stopService(new Intent(context, AppWidgetService.class));
	}
}
