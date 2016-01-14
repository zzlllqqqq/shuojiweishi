package com.atguigu.ms.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.atguigu.ms.R;
import com.atguigu.ms.util.MSUtils;
import com.atguigu.ms.util.SpUtils;

/**
 * 设置向导3
 * @author 张晓飞
 *
 */
public class Guide3Activity extends Activity {

	private EditText et_guide3_number;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide3);
		
		et_guide3_number = (EditText) findViewById(R.id.et_guide3_number);
		//显示保存的安全号码
		String savedSafeNumber = SpUtils.getInstance(this).getString(SpUtils.SAFE_NUMBER, null);
		if(savedSafeNumber!=null) {
			et_guide3_number.setText(savedSafeNumber);
		}
	}
	
	public void pre(View v) {
		finish();
		startActivity(new Intent(this, Guide2Activity.class));
		this.overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
	public void next(View v) {
		String safeNumber = et_guide3_number.getText().toString();
		if(safeNumber.trim().equals("")) {
			MSUtils.showMsg(this, "必须指定安全号码");
			return;
		}
		
		//保存安全号码
		SpUtils.getInstance(this).save(SpUtils.SAFE_NUMBER, safeNumber);
		
		finish();
		startActivity(new Intent(this, Guide4Activity.class));
		this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	
	public void showContacts(View v) {
		startActivityForResult(new Intent(this, ContactListActivity.class), 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==1 && resultCode==RESULT_OK) {
			String number = data.getStringExtra("NUMBER");
			et_guide3_number.setText(number);
		}
	}
}
