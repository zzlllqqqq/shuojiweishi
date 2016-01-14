package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.atguigu.shoujiweishi.util.SmsUtils;

public class AToolActivity extends Activity implements View.OnClickListener {

    private TextView tv_atool_lock;
    private TextView tv_atool_address;
    private TextView tv_atool_number;
    private TextView tv_atool_reset;
    private TextView tv_atool_backup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
        TextView textView = (TextView) findViewById(R.id.tv_title);
        textView.setText("高级工具");
        tv_atool_backup = (TextView) findViewById(R.id.tv_atool_backup);
        tv_atool_reset = (TextView) findViewById(R.id.tv_atool_reset);
        tv_atool_number = (TextView) findViewById(R.id.tv_atool_number);
        tv_atool_address = (TextView) findViewById(R.id.tv_atool_address);
        tv_atool_lock = (TextView) findViewById(R.id.tv_atool_lock);

        tv_atool_backup.setOnClickListener(this);
        tv_atool_reset.setOnClickListener(this);
        tv_atool_number.setOnClickListener(this);
        tv_atool_address.setOnClickListener(this);
        tv_atool_lock.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == tv_atool_address) {
            startActivity(new Intent(this, AddressQueryActivity.class));
        }  else if (v == tv_atool_lock) {
            startActivity(new Intent(this, AppLockActivity.class));
        } else if (v == tv_atool_number) {
            startActivity(new Intent(this, NumberQueryActivity.class));
        } else if (v == tv_atool_reset) {
            SmsUtils.reset(this);
        } else if (v == tv_atool_backup) {
            SmsUtils.backup(this);
        }
    }
}
