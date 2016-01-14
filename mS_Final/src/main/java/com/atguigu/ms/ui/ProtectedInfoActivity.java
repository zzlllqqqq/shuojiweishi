package com.atguigu.ms.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.util.SpUtils;
/**
 * 防盗设置信息界面
 * @author 张晓飞
 *
 */
public class ProtectedInfoActivity extends Activity {

	private TextView tv_safe_number;
	private ImageView iv_protect_icon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_protected_info);
		
		tv_safe_number = (TextView) findViewById(R.id.tv_safe_number);
		iv_protect_icon = (ImageView) findViewById(R.id.iv_protect_icon);
		
		String safeNumber = SpUtils.getInstance(this).getString(SpUtils.SAFE_NUMBER, null);
		tv_safe_number.setText(safeNumber);
		boolean protect = SpUtils.getInstance(this).getBoolean(SpUtils.PROTECT, false);
		if(protect) {
			iv_protect_icon.setImageResource(R.drawable.lock);
		}
	}
	
	public void startConfig(View v) {
		startActivity(new Intent(this, Guide1Activity.class));
	}
}
