package com.atguigu.weishi;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.atguigu.weishi.receiver.MyAdminReceiver;
import com.atguigu.weishi.util.MsUtils;
import com.atguigu.weishi.util.SPUtils;

public class Setup4Activity extends Activity {

    private TextView tv_setup4;
    private CheckBox cb_setup4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        tv_setup4 = (TextView)findViewById(R.id.tv_setup4);
        cb_setup4 = (CheckBox)findViewById(R.id.cb_setup4);

        if(isActive()) {
            tv_setup4.setText("已激活, 防盗功能可以使用");
            tv_setup4.setTextColor(Color.BLACK);
        }
        boolean protect = SPUtils.getInstanse(this).getValue(SPUtils.PROTECT, false);
        if (protect) {
            cb_setup4.setChecked(true);
            cb_setup4.setText("已经开启保护");
            cb_setup4.setTextColor(Color.BLACK);
        }
        cb_setup4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//不选中-->选中
                    cb_setup4.setText("已经开启保护");
                    cb_setup4.setTextColor(Color.BLACK);
                    SPUtils.getInstanse(Setup4Activity.this).save(SPUtils.PROTECT, true);
                } else {//选中-->不选中
                    cb_setup4.setText("未开启保护");
                    cb_setup4.setTextColor(Color.RED);
                    SPUtils.getInstanse(Setup4Activity.this).remove(SPUtils.PROTECT);
                }
            }
        });
    }

    public void previous(View v) {
        finish();
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);

        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void confirm(View v) {
        //如果没有激活, 需要启动激活
        if (!isActive()) {
            ComponentName componentName = new ComponentName(this, MyAdminReceiver.class);
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(intent, 1);
        } else {//如果已经激活, 直接进入下一页
            //保存完成配置的标识
            SPUtils.getInstanse(this).save(SPUtils.CONFIGURE, true);

            Intent intent = new Intent(this, ProtectinfoActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            SPUtils.getInstanse(this).save(SPUtils.CONFIGURE, null);
            Intent intent = new Intent(this, ProtectinfoActivity.class);
            startActivity(intent);
            finish();
        } else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            MsUtils.showMsg(this, "必须激活");
        }
    }

    /**
     * 判断是否已经激活
     *
     * @return
     */
    private boolean isActive() {
        DevicePolicyManager manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName name = new ComponentName(this, MyAdminReceiver.class);
        return manager.isAdminActive(name);
    }
}
