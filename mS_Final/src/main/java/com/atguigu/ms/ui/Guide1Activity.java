package com.atguigu.ms.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.atguigu.ms.R;

/**
 * 设置向导1
 * @author 张晓飞
 *
 */
public class Guide1Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide1);
	}

	public void next(View v) {
		finish();
		startActivity(new Intent(this, Guide2Activity.class));
		
		this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
}
