package com.atguigu.weishi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.atguigu.weishi.dao.BlackNumberDao;
import com.atguigu.weishi.util.GpsUtils;
import com.atguigu.weishi.util.MsUtils;
import com.atguigu.weishi.util.SPUtils;

/**
 * Created by admin on 2015/12/28.
 */
public class SmsReceiver extends BroadcastReceiver {
    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        byte[] pdu = (byte[]) pdus[0];
        SmsMessage smsMessage = SmsMessage.createFromPdu(pdu);
        String content = smsMessage.getMessageBody();
        String number = smsMessage.getDisplayOriginatingAddress();
        BlackNumberDao blackNumberDao = new BlackNumberDao(context);
        boolean black = blackNumberDao.isBlack(number);
        if(black) {
            abortBroadcast();
        }


        boolean protect = SPUtils.getInstanse(context).getValue(SPUtils.PROTECT, false);
        String safeNumber = SPUtils.getInstanse(context).getValue(SPUtils.SAFE_NUMBER, null);
        if(!protect) {
            return;
        }

        if("#alarm#".equals(content)){
            abortBroadcast();
            MsUtils.playAlert(context);
        } else if("#wipedata#".equals(content)){
            abortBroadcast();
            MsUtils.resetPhone(context);
        } else if("#lockscreen#".equals(content)){
            abortBroadcast();
            MsUtils.lockScreen(context);
        } else if("#location#".equals(content)){
            abortBroadcast();
            GpsUtils.location(context, safeNumber);
        }
    }
}
