package com.atguigu.weishi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.atguigu.weishi.util.MsUtils;
import com.atguigu.weishi.util.SPUtils;

/**
 * Created by admin on 2015/12/28.
 */
public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String number = SPUtils.getInstanse(context).getValue(SPUtils.SIM_NUMBER, null);
        String safeNumber = SPUtils.getInstanse(context).getValue(SPUtils.SAFE_NUMBER, null);
        if(number == null || safeNumber == null) {
            return;
        }
        String newSimNumber = MsUtils.getSimNumber(context) + "s";
        if(!number.equals(newSimNumber)) {
            MsUtils.sendSms(safeNumber, "sim change, care care...");
        }
    }
}
