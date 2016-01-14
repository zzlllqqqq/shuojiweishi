package com.atguigu.shoujiweishi.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.atguigu.shoujiweishi.service.UpdateWidgetService;

/**
 * Created by admin on 2016/1/6.
 * widget  小部件
 */
public class ProcessWidget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {//长按产生一个桌面widget，既是桌面快捷方式
        super.onEnabled(context);
        //启动更新widget的service
        context.startService(new Intent(context, UpdateWidgetService.class));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //停止更新widget的service
        context.stopService(new Intent(context, UpdateWidgetService.class));
    }
}
