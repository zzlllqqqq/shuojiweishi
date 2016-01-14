package com.atguigu.weishi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.weishi.util.SPUtils;

public class ProtectinfoActivity extends Activity {

    private TextView tv_info_number;
    private ImageView iv_info_lock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protectinfo);
        tv_info_number = (TextView)findViewById(R.id.tv_info_number);
        iv_info_lock = (ImageView)findViewById(R.id.iv_info_lock);

        String safeNumber = SPUtils.getInstanse(this).getValue(SPUtils.SAFE_NUMBER, null);
        if(safeNumber!=null) {
            tv_info_number.setText(safeNumber);
        }

        boolean protect = SPUtils.getInstanse(this).getValue(SPUtils.PROTECT, false);
        if(protect) {
            iv_info_lock.setImageResource(R.drawable.lock);
        }
    }

    public void resetConfigure(View v) {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        finish();
    }
}
