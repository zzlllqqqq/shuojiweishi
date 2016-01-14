package com.atguigu.shoujiweishi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.atguigu.shoujiweishi.dao.BlackNumberDao;
import com.atguigu.shoujiweishi.util.GpsUtils;
import com.atguigu.shoujiweishi.util.MSUtils;
import com.atguigu.shoujiweishi.util.SPUtils;

/**
 * Created by admin on 2015/12/28.
 */
public class SmsReceiver extends BroadcastReceiver {
    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //1. 获取短信数据(号码/内容)
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        byte[] pdu = (byte[]) pdus[0];
        SmsMessage smsMessage = SmsMessage.createFromPdu(pdu);
        //获取短信内容
        String content = smsMessage.getMessageBody();
        //获取短信号码
        String number = smsMessage.getOriginatingAddress();
        boolean black = new BlackNumberDao(context).isBlack(number);
        if(black) {
            //如果是黑名单号那么直接拦截
            abortBroadcast();
        }
        //2. 判断防盗保护是否开启, 如果没有结束
        boolean protect = SPUtils.getInstance(context).getValue(SPUtils.PROTECT, false);
        if(!protect) {
            return;
        }
        String safeNumber = SPUtils.getInstance(context).getValue(SPUtils.SAFE_NUMBER, null);
        if("#alarm#".equals(content)){
            abortBroadcast();
            MSUtils.playAlert(context);
        } else if("#wipedata#".equals(content)){
            abortBroadcast();
            MSUtils.resetPhone(context);
        } else if("#lockscreen#".equals(content)){
            abortBroadcast();
            MSUtils.lockScreen(context);
        } else if("#location#".equals(content)){
            abortBroadcast();
            GpsUtils.location(context, safeNumber);
        }
    }
}
