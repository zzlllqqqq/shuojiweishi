package com.atguigu.ms.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.atguigu.ms.R;
import com.atguigu.ms.util.MSUtils;
import com.atguigu.ms.util.SpUtils;

/**
 * 设置向导2
 * @author 张晓飞
 *
 */
public class Guide2Activity extends Activity {

	private CheckBox cb_guide2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide2);
		
		cb_guide2 = (CheckBox) findViewById(R.id.cb_guide2);
		//从sp中取出保存的sim卡序列号
		String simNumber = SpUtils.getInstance(this).get(SpUtils.SIM_NUMBER, null);
		//如存在就需要更新cb_guide2的显示
		if(simNumber!=null) {
			cb_guide2.setChecked(true);
			cb_guide2.setTextColor(Color.BLACK);
			cb_guide2.setText("已经绑定SIM卡");
		}
		
		//设置监听
		cb_guide2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {//选中, 保存
					String simNumber = MSUtils.getSimNumber(getApplicationContext());
					SpUtils.getInstance(getApplicationContext()).save(SpUtils.SIM_NUMBER, simNumber);
					cb_guide2.setChecked(true);
					cb_guide2.setTextColor(Color.BLACK);
					cb_guide2.setText("已经绑定SIM卡");
				} else {//不选中, 移除
					SpUtils.getInstance(getApplicationContext()).remove(SpUtils.SIM_NUMBER);
					cb_guide2.setChecked(false);
					cb_guide2.setTextColor(Color.RED);
					cb_guide2.setText("还没有绑定SIM卡");
				}
			}
		});
		
	}
	
	public void pre(View v) {
		finish();
		startActivity(new Intent(this, Guide1Activity.class));
		this.overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
	public void next(View v) {
		//如果cb_guide2没有选中, 不能过去
		if(!cb_guide2.isChecked()) {
			MSUtils.showMsg(this, "必须绑定SIM卡");
			return;
		}
		
		finish();
		startActivity(new Intent(this, Guide3Activity.class));
		this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
}
