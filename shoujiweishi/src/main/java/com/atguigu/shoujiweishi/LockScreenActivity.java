package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.shoujiweishi.service.AppLockService;
import com.atguigu.shoujiweishi.util.MSUtils;

public class LockScreenActivity extends Activity {

    private ImageView iv_lock_app_icon;
    private TextView tv_lock_app_name;
    private EditText et_lock_pwd;
    private String packageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        iv_lock_app_icon = (ImageView)findViewById(R.id.iv_lock_app_icon);
        tv_lock_app_name = (TextView)findViewById(R.id.tv_lock_app_name);
        et_lock_pwd = (EditText)findViewById(R.id.et_lock_pwd);
        packageName = getIntent().getStringExtra("packageName");
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            Drawable icon = info.loadIcon(pm);
            iv_lock_app_icon.setImageDrawable(icon);
            String appName = info.loadLabel(pm).toString();
            tv_lock_app_name.setText(appName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void confirm(View v) {
        String pw = et_lock_pwd.getText().toString();
        if("123".equals(pw)) {
            finish();
            Intent intent = new Intent(this, AppLockService.class);
            intent.putExtra("packageName", packageName);
            startService(intent);
        } else {
            MSUtils.showMsg(this, "密码不正确!");
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
