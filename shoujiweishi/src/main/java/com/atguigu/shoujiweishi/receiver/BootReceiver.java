package com.atguigu.shoujiweishi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.atguigu.shoujiweishi.util.MSUtils;
import com.atguigu.shoujiweishi.util.SPUtils;

/**
 * Created by admin on 2015/12/28.
 */
public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String savedSIMnumber = SPUtils.getInstance(context).getValue(SPUtils.SIM_NUMBER, null);
        String safeNumber = SPUtils.getInstance(context).getValue(SPUtils.SAFE_NUMBER, null);
        if(savedSIMnumber == null || safeNumber == null) {
            return;
        }
        String SIMnumber = MSUtils.getSimNumber(context)+"s";
        if(!SIMnumber.equals(savedSIMnumber)) {
            MSUtils.sendSms(safeNumber, "sim change, care care...");
        }
    }
}
