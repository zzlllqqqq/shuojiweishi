package com.atguigu.ms.ui;

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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.receiver.MsDeviceAdminReceiver;
import com.atguigu.ms.util.MSUtils;
import com.atguigu.ms.util.SpUtils;

/**
 * 设置向导4
 * 
 * @author 张晓飞
 *
 */
public class Guide4Activity extends Activity {

	private CheckBox cb_guide4;
	private TextView tv_guide4_message;
	private boolean active = false;// 标识是否激活

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide4);

		cb_guide4 = (CheckBox) findViewById(R.id.cb_guide4);
		// 从sp中取出保存的是否开启保存的标识(protect)
		boolean protect = SpUtils.getInstance(this).get(SpUtils.PROTECT, false);
		// 如存在就需要更新cb_guide2的显示
		if (protect) {
			cb_guide4.setChecked(true);
			cb_guide4.setTextColor(Color.BLACK);
			cb_guide4.setText("手机已经开启保护");
		}

		// 设置监听
		cb_guide4.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {// 选中, 保存true
					SpUtils.getInstance(getApplicationContext()).save(
							SpUtils.PROTECT, true);
					cb_guide4.setChecked(true);
					cb_guide4.setTextColor(Color.BLACK);
					cb_guide4.setText("手机已经开启保护");
				} else {// 不选中, 移除
					SpUtils.getInstance(getApplicationContext()).remove(
							SpUtils.PROTECT);
					cb_guide4.setChecked(false);
					cb_guide4.setTextColor(Color.RED);
					cb_guide4.setText("手机没有开启保护");
				}
			}
		});

		// 如果设置已经激活, 更新显示文本
		tv_guide4_message = (TextView) findViewById(R.id.tv_guide4_message);
		active = isActive();
		if (active) {
			tv_guide4_message.setText("应用已经激活!");
			tv_guide4_message.setTextColor(Color.BLACK);
		}
	}

	/**
	 * 判断是否激活
	 * 
	 * @return
	 */
	private boolean isActive() {
		DevicePolicyManager manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName name = new ComponentName(this,
				MsDeviceAdminReceiver.class);
		return manager.isAdminActive(name);
	}

	public void pre(View v) {
		finish();
		startActivity(new Intent(this, Guide3Activity.class));
		this.overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	public void confirm(View v) {
		if (!active) {// 当前还没有激活, 需要立即去启动激活
			ComponentName componentName = new ComponentName(this,MsDeviceAdminReceiver.class);
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
			this.startActivityForResult(intent, 1);
		} else {
			finish();
			SpUtils.getInstance(this).save(SpUtils.CONFIGED, true);
			startActivity(new Intent(this, ProtectedInfoActivity.class));
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK) {//确定激活了
			finish();
			SpUtils.getInstance(this).save(SpUtils.CONFIGED, true);
			startActivity(new Intent(this, ProtectedInfoActivity.class));
			MSUtils.showMsg(this, "应用已激活");
		} else {//取消了激活
			MSUtils.showMsg(this, "应用必须激活");
		}
	}
}
