package com.atguigu.ms.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.util.SmsUtils;

/**
 * 高级工具界面
 * @author 张晓飞
 *
 */
public class AToolActivity extends Activity implements OnClickListener {

	private TextView tv_title;
	private TextView tv_atool_lock;
	private TextView tv_atool_address;
	private TextView tv_atool_common_number;
	private TextView tv_atool_restore;
	private TextView tv_atool_backup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		init();
	}

	private void init() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("高级工具");
		tv_atool_lock = (TextView) findViewById(R.id.tv_atool_lock);
		tv_atool_address = (TextView) findViewById(R.id.tv_atool_address);
		tv_atool_common_number = (TextView) findViewById(R.id.tv_atool_common_number);
		tv_atool_restore = (TextView) findViewById(R.id.tv_atool_restore);
		tv_atool_backup = (TextView) findViewById(R.id.tv_atool_backup);
		
		tv_atool_lock.setOnClickListener(this);
		tv_atool_address.setOnClickListener(this);
		tv_atool_common_number.setOnClickListener(this);
		tv_atool_restore.setOnClickListener(this);
		tv_atool_backup.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v==tv_atool_lock) {
			startActivity(new Intent(this, AppLockActivity.class));
		} else if(v==tv_atool_address) {
			startActivity(new Intent(this, QueryAddressActivity.class));
		} else if(v==tv_atool_common_number) {
			startActivity(new Intent(this, CommonNumberActivity.class));
		} else if(v==tv_atool_restore) {//还原
			SmsUtils.restore(this);
		} else if(v==tv_atool_backup) {//备份
			SmsUtils.backup(this);
		}
	}
}
