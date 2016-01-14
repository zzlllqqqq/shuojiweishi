package com.atguigu.ms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.atguigu.ms.util.MSUtils;
import com.atguigu.ms.util.SpUtils;
/**
 * 监听开机广播的receiver
 * @author 张晓飞
 *
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("TAG", "onReceive()");
		//判断是否开启保护,如果没有开启结束了
		boolean protect = SpUtils.getInstance(context).getBoolean(SpUtils.PROTECT, false);
		if(!protect)
			return;
		
		String savedSimNumber = SpUtils.getInstance(context).get(SpUtils.SIM_NUMBER, null);
		String simNumber = MSUtils.getSimNumber(context)+"9";
		//判断是否相同 
		if(!savedSimNumber.equals(simNumber)) {
			//得到安全号码
			String safeNumber = SpUtils.getInstance(context).get(SpUtils.SAFE_NUMBER, null);
			//发送短信
			MSUtils.sendSms(safeNumber);
		}
	}
}
