package com.atguigu.ms.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.service.AppLockService;
import com.atguigu.ms.util.MSUtils;

/**
 * 给应用解锁的界面
 * @author 张晓飞
 *
 */
public class LockScreenActivity extends Activity {

	private EditText et_lock_pwd;
	private TextView tv_lock_app_name;
	private ImageView iv_lock_app_icon;
	private String packageName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_screen);
		
		et_lock_pwd =(EditText) findViewById(R.id.et_lock_pwd);
		tv_lock_app_name =(TextView) findViewById(R.id.tv_lock_app_name);
		iv_lock_app_icon =(ImageView) findViewById(R.id.iv_lock_app_icon);
		
		//得到锁定应用的包名
		packageName = getIntent().getStringExtra("packagename");
		
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			iv_lock_app_icon.setImageDrawable(icon);
			String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
			tv_lock_app_name.setText(appName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void confirm(View v) {//pwd=123
		String pwd = et_lock_pwd.getText().toString();
		if("123".equals(pwd)) {
			finish();
			
			//通过startService的方式将packageName传给Service
			Intent intent = new Intent(this, AppLockService.class);
			intent.putExtra("PACKAGE_NAME", packageName);
			startService(intent);
		} else {
			MSUtils.showMsg(this, "输入的密码不正确!");
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			return true; //点击back不退出界面
		}
		return super.onKeyUp(keyCode, event);//默认就会关闭
	}
}
