package com.atguigu.ms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.atguigu.ms.dao.BlackNumberDao;
import com.atguigu.ms.util.GpsUtils;
import com.atguigu.ms.util.MSUtils;
import com.atguigu.ms.util.SpUtils;

/**
 * 监听接收到短信的广播的receiver
 * 
 * @author 张晓飞
 *
 */
public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("TAG", "SmsReceiver onReceive()");
		
		
		//读取短信内容,判断是否是防盗指令
		Bundle bundle = intent.getExtras();
		Object[] pdus = (Object[]) bundle.get("pdus");
		byte[] pdu = (byte[]) pdus[0];
		SmsMessage smsMessage = SmsMessage.createFromPdu(pdu);
		String phoneNumber = smsMessage.getOriginatingAddress();
		String message = smsMessage.getMessageBody();
		Log.e("TAG", phoneNumber+":"+message);
				
		//拦截黑名单短信
		//1. 判断是否是黑名单号
		boolean black = new BlackNumberDao(context).isBlack(phoneNumber);
		//2. 如果是, 拦截
		if(black) {
			Log.i("TAG", "拦截到一个黑名单短信 ");
			abortBroadcast();//中断广播
		}
				
		// 判断是否开启保护,如果没有开启结束了
		boolean protect = SpUtils.getInstance(context).getBoolean(SpUtils.PROTECT, false);
		if (!protect)
			return;

		String safeNumber = "1555521"+SpUtils.getInstance(context).getString(SpUtils.SAFE_NUMBER, null);
		if(safeNumber.equals(phoneNumber)) {
			if("#lock#".equals(message)) {//锁屏
				MSUtils.lock(context);
				abortBroadcast();//中断广播
			} else if("#reset#".equals(message)) {//删除数据
				MSUtils.reset(context);
				abortBroadcast();//中断广播
			} else if("#alarm#".equals(message)) {//播放报警音乐
				MSUtils.playAlarm(context);
				abortBroadcast();//中断广播
			} else if("#location#".equals(message)) {//gps定位
				GpsUtils.location(context, safeNumber);
				abortBroadcast();//中断广播
			}
		}
	}

}
