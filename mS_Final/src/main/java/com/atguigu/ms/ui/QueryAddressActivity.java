package com.atguigu.ms.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.dao.AddressDao;

/**
 * 查询号码归属地界面
 * @author 张晓飞
 *
 */
public class QueryAddressActivity extends Activity {

	private EditText et_address_number;
	private TextView tv_address_result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);
		
		et_address_number = (EditText) findViewById(R.id.et_address_number);
		tv_address_result = (TextView) findViewById(R.id.tv_address_result);
	}
	
	public void queryAddress(View v) {
		//1. 得到输入的号码
		String number = et_address_number.getText().toString();
		if(number.trim().equals("")) {//如果没有输入就抖动
			//显示抖动动画
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_address_number.startAnimation(animation);
		} else {
			//2. 查询数据库得到对应的归属地
			String address = new AddressDao(this).getAddress(number);
			//3. 显示归属地
			tv_address_result.setText(address);
		}
		
	}
}
