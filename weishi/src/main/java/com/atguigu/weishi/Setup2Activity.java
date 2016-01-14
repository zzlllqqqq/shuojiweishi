package com.atguigu.weishi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.atguigu.weishi.util.MsUtils;
import com.atguigu.weishi.util.SPUtils;

public class Setup2Activity extends Activity {

    private CheckBox cb_setup2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        cb_setup2 = (CheckBox)findViewById(R.id.cb_setup2);
        final String number = MsUtils.getSimNumber(this);

        String SNnumber = SPUtils.getInstanse(this).getValue(SPUtils.SIM_NUMBER, null);
        if(SNnumber!=null) {
            cb_setup2.setChecked(true);
            cb_setup2.setText("已绑定SIM卡");
            cb_setup2.setTextColor(Color.BLACK);
        }

        cb_setup2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    cb_setup2.setText("已绑定SIM卡");
                    cb_setup2.setTextColor(Color.BLACK);
                    SPUtils.getInstanse(Setup2Activity.this).save(SPUtils.SIM_NUMBER, number);
                } else {
                    cb_setup2.setText("没有绑定SIM卡");
                    cb_setup2.setTextColor(Color.RED);
                    SPUtils.getInstanse(Setup2Activity.this).remove(SPUtils.SIM_NUMBER);
                }
            }
        });
    }

    public void previous(View v) {
        finish();
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void next(View v) {
        if(!cb_setup2.isChecked()) {
            MsUtils.showMsg(this, "必须绑定SIM卡");
            return;
        }


        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
