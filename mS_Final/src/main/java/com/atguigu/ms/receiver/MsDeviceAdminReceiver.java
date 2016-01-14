package com.atguigu.ms.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 应用激活相关的广播接收器
 * @author 张晓飞
 *
 */
public class MsDeviceAdminReceiver extends DeviceAdminReceiver {

	//当屏幕被锁定后, 用户输入正确的密码解锁时调用
	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		Log.i("TAG", "onPasswordSucceeded()");
	}
}
