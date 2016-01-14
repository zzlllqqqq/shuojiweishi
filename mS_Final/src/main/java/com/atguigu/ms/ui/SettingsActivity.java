package com.atguigu.ms.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.service.AddressService;
import com.atguigu.ms.service.AppLockService;
import com.atguigu.ms.util.MSUtils;
import com.atguigu.ms.util.SpUtils;
/**
 * 设置中心界面
 * @author 张晓飞
 *
 */
public class SettingsActivity extends Activity implements OnClickListener {

	private TextView tv_title;
	private LinearLayout ll_settings_style;
	private TextView tv_settings_style;
	private TextView tv_settings_location;
	private String[] styleNames;
	private int index;
	
	//来电归属地
	private LinearLayout ll_settings_address;
	private TextView tv_settings_address;
	private CheckBox cb_settings_address;
	
	//程序锁服务的
	private LinearLayout ll_settings_lock;
	private TextView tv_settings_lock;
	private CheckBox cb_settings_lock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		init();
	}

	private void init() {
		
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("设置中心");
		
		styleNames = getResources().getStringArray(R.array.styleNames);
		ll_settings_style = (LinearLayout) findViewById(R.id.ll_settings_style);
		tv_settings_style = (TextView) findViewById(R.id.tv_settings_style);
		tv_settings_location =(TextView) findViewById(R.id.tv_settings_location);
		
		ll_settings_address = (LinearLayout) findViewById(R.id.ll_settings_address);
		tv_settings_address = (TextView) findViewById(R.id.tv_settings_address);
		cb_settings_address = (CheckBox) findViewById(R.id.cb_settings_address);
		
		
		//判断来电归属地服务是否启动, 如果启动了更新显示
		boolean running = MSUtils.isServiceRunning(this, AddressService.class.getName());
		if(running) {
			cb_settings_address.setChecked(true);
			tv_settings_address.setText("归属地服务已开启");
			tv_settings_address.setTextColor(Color.BLACK);
		}
		
		ll_settings_lock = (LinearLayout) findViewById(R.id.ll_settings_lock);
		tv_settings_lock = (TextView) findViewById(R.id.tv_settings_lock);
		cb_settings_lock = (CheckBox) findViewById(R.id.cb_settings_lock);
		
		
		//判断来程序锁服务是否启动, 如果启动了更新显示
		running = MSUtils.isServiceRunning(this, AppLockService.class.getName());
		if(running) {
			cb_settings_lock.setChecked(true);
			tv_settings_lock.setText("程序锁服务已开启");
			tv_settings_lock.setTextColor(Color.BLACK);
		}
		
		//读取保存的下标显示背景样式
		index = SpUtils.getInstance(this).get(SpUtils.STYLE_INDEX, 0);
		tv_settings_style.setText(styleNames[index]);
		
		
		ll_settings_style.setOnClickListener(this);
		tv_settings_location.setOnClickListener(this);
		ll_settings_address.setOnClickListener(this);
		ll_settings_lock.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v==ll_settings_style) {
			//显示带单选列表的dialog
			new AlertDialog.Builder(this)
					.setTitle("选择样式")
					.setSingleChoiceItems(styleNames, index, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {//which就是选择的下标
							//更新内存
							index = which;
							//保存
							SpUtils.getInstance(getApplicationContext()).save(SpUtils.STYLE_INDEX, index);
							//更新界面
							dialog.dismiss();
							tv_settings_style.setText(styleNames[index]);
						}
					})
					.show();
		} else if(v==tv_settings_location) {
			startActivity(new Intent(this, AdressLocationSetActivity.class));
		} else if(v==ll_settings_address) {
			if(cb_settings_address.isChecked()) {//变为不选中, 停止服务, 更新文本
				cb_settings_address.setChecked(false);
				stopService(new Intent(this, AddressService.class));
				tv_settings_address.setText("归属地服务未开启");
				tv_settings_address.setTextColor(Color.RED);
			} else { //变为选中, 启动服务, 更新文本
				cb_settings_address.setChecked(true);
				startService(new Intent(this, AddressService.class));
				tv_settings_address.setText("归属地服务已开启");
				tv_settings_address.setTextColor(Color.BLACK);
			}
		} else if(v==ll_settings_lock) {
			if(cb_settings_lock.isChecked()) {//变为不选中, 停止服务, 更新文本
				cb_settings_lock.setChecked(false);
				stopService(new Intent(this, AppLockService.class));
				tv_settings_lock.setText("程序锁服务未开启");
				tv_settings_lock.setTextColor(Color.RED);
			} else { //变为选中, 启动服务, 更新文本
				cb_settings_lock.setChecked(true);
				startService(new Intent(this, AppLockService.class));
				tv_settings_lock.setText("程序锁服务已开启");
				tv_settings_lock.setTextColor(Color.BLACK);
			}
		} 
	}
}
