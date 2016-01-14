package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.atguigu.shoujiweishi.service.AddressService;
import com.atguigu.shoujiweishi.service.AppLockService;
import com.atguigu.shoujiweishi.util.MSUtils;
import com.atguigu.shoujiweishi.util.SPUtils;

public class SettingsActivity extends Activity {

    private TextView tv_settings_style;
    private TextView tv_settings_address;
    private CheckBox cb_settings_address;
    private String[] styleNames;
    private int styleIndex;
    private TextView tv_settings_lock;
    private CheckBox cb_settings_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tv_settings_style = (TextView) findViewById(R.id.tv_settings_style);
        tv_settings_address = (TextView) findViewById(R.id.tv_settings_address);
        cb_settings_address = (CheckBox) findViewById(R.id.cb_settings_address);

        styleIndex = SPUtils.getInstance(this).getValue(SPUtils.STYLE_INDEX, 0);
        styleNames = getResources().getStringArray(R.array.style_names);
        tv_settings_style.setText(styleNames[styleIndex]);

        boolean isrunning = MSUtils.isServiceRunning(this, AddressService.class.getName());
        if(isrunning) {
            cb_settings_address.setChecked(true);
            tv_settings_address.setText("归属地服务已开启");
            tv_settings_address.setTextColor(Color.BLACK);
        }

        tv_settings_lock = (TextView)findViewById(R.id.tv_settings_lock);
        cb_settings_lock = (CheckBox)findViewById(R.id.cb_settings_lock);
        isrunning = MSUtils.isServiceRunning(this, AppLockService.class.getName());
        if(isrunning) {
            cb_settings_lock.setChecked(true);
            tv_settings_lock.setText("程序锁服务已开启");
            tv_settings_lock.setTextColor(Color.BLACK);
        }
    }

    public void setAddressLocation(View v) {
        startActivity(new Intent(this, SetAddressLocationActivity.class));
    }

    public void setAddressStyle(View v) {
        new AlertDialog.Builder(this).setTitle("选择样式").setSingleChoiceItems(styleNames, styleIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                styleIndex = which;
                dialog.dismiss();
                tv_settings_style.setText(styleNames[styleIndex]);
                SPUtils.getInstance(getApplicationContext()).save(SPUtils.STYLE_INDEX, which);
            }
        }).show();
    }

    public void setAddressService(View v) {
        boolean checked = cb_settings_address.isChecked();
        if(checked){
            cb_settings_address.setChecked(false);
            tv_settings_address.setText("归属地服务未开启");
            tv_settings_address.setTextColor(Color.RED);
            stopService(new Intent(this, AddressService.class));
        } else {
            cb_settings_address.setChecked(true);
            tv_settings_address.setText("归属地服务已开启");
            tv_settings_address.setTextColor(Color.BLACK);
            startService(new Intent(this, AddressService.class));
        }

    }

    public void setLockService(View v) {
        //得到当前勾选状态
        boolean checked = cb_settings_lock.isChecked();
        //如果勾选
        if(checked){
            cb_settings_lock.setChecked(false);
            tv_settings_lock.setText("程序锁服务未开启");
            tv_settings_lock.setTextColor(Color.RED);
            //停止服务
            stopService(new Intent(this, AppLockService.class));
        } else {//如果没勾选
            cb_settings_lock.setChecked(true);
            tv_settings_lock.setText("程序锁服务已开启");
            tv_settings_lock.setTextColor(Color.BLACK);
            //开启服务
            startService(new Intent(this, AppLockService.class));
        }
    }
}
