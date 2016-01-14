package com.atguigu.ms.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.adapter.MainAdapter;
import com.atguigu.ms.adapter.MainAdapter.ViewHolder;
import com.atguigu.ms.util.MSUtils;
import com.atguigu.ms.util.SpUtils;

/**
 * 应用主界面
 * @author 张晓飞
 *
 */
public class MainActivity extends Activity implements OnItemLongClickListener, OnItemClickListener, OnClickListener {

	//GridView BaseAapter
	
	private GridView gv_main;
	private MainAdapter adapter;
	//private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//初始化sp
		//sp = getSharedPreferences("ms", Context.MODE_PRIVATE);
		
		TextView titleTV = (TextView) findViewById(R.id.tv_title);
		titleTV.setText("手机卫士");
		
		gv_main = (GridView) findViewById(R.id.gv_main);
		adapter = new MainAdapter(this);
		gv_main.setAdapter(adapter);
		
		//设置长按监听
		gv_main.setOnItemLongClickListener(this);
		//设置item的点击监听
		gv_main.setOnItemClickListener(this);
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if(position==0) {
			final EditText editText = new EditText(this);
			//TextView textView = (TextView) view.findViewById(R.id.tv_item_name);
			MainAdapter.ViewHolder holder = (ViewHolder) view.getTag();
			final TextView textView = holder.textView;
			//提示当前显示的名称
			editText.setHint(textView.getText().toString());
			new AlertDialog.Builder(this)
					.setTitle("修改名称")
					.setView(editText)
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//得到新名称
							String newName = editText.getText().toString();
							//显示输入的名称
							textView.setText(newName);
							//保存新名称(sp中)
							//sp.edit().putString("lost_name", newName).commit();
							SpUtils.getInstance(getApplicationContext()).save(SpUtils.LOST_NAME, newName);
						}})
					.setNegativeButton("取消", null)
					.show();
		}
		return true;//不会触发点击事件点
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0://手机防盗
			showSetOrLoginDialog();
			break;
		case 1:
			startActivity(new Intent(this, BlackManagerActivity.class));
			break;
		case 2://软件管理
			startActivity(new Intent(this, AppManagerActivity.class));
			break;
		case 3:
			startActivity(new Intent(this, TrafficManagerActivity.class));
			break;
		case 4://进程管理
			startActivity(new Intent(this, TaskManagerActivity.class));
			break;
		case 5:
			startActivity(new Intent(this, AntiVirusActivity.class));
			break;
		case 6://缓存清理
			startActivity(new Intent(this, CleanCacheActivity.class));
			break;
		case 7: //高级工具
			startActivity(new Intent(this, AToolActivity.class));
			break;
		case 8:
			startActivity(new Intent(this, SettingsActivity.class));
			break;

		default:
			break;
		}
	}

	/**
	 * 显示防盗密码设置或登陆的dialog
	 * password=xxx
	 */
	private void showSetOrLoginDialog() {
		//1. 取出sp中保存的密码(密文)
		//String savePwd = sp.getString("password", null);
		String savePwd = SpUtils.getInstance(this).getString(SpUtils.PASSWORD, null);
		//2. 如果没有, 显示设置的dialog
		if(savePwd==null) {
			showPwdSetDialog();
		} else {
			//3. 如果有, 显示登陆的dialog
			showLoginDialog(savePwd);
		}
	}

	
	private EditText et_pwd_login_pwd;
	private Button btn_pwd_login_confirm;
	private Button btn_pwd_login_cancel;
	private AlertDialog loginDailog;
	/**
	 * 显示登陆的dialog
	 * @param savedPwd
	 */
	private void showLoginDialog(String savedPwd) {
		View view = View.inflate(this, R.layout.pwd_login_dialog_view, null);
		et_pwd_login_pwd = (EditText) view.findViewById(R.id.et_pwd_login_pwd);
		btn_pwd_login_confirm = (Button) view.findViewById(R.id.btn_pwd_login_confirm);
		btn_pwd_login_cancel = (Button) view.findViewById(R.id.btn_pwd_login_cancel);
		btn_pwd_login_confirm.setOnClickListener(this);
		btn_pwd_login_cancel.setOnClickListener(this);
		//将保存的密码缓存到视图中
		btn_pwd_login_confirm.setTag(savedPwd);
		loginDailog = new AlertDialog.Builder(this)
						.setView(view)
						.show();
	}

	private EditText et_pwd_set_pwd;
	private EditText et_pwd_set_pwd2;
	private Button btn_pwd_set_confirm;
	private Button btn_pwd_set_cancel;
	private AlertDialog setDailog;
	/**
	 * 显示设置的dialog
	 */
	private void showPwdSetDialog() {
		View view = View.inflate(this, R.layout.pwd_set_dialog_view, null);
		et_pwd_set_pwd = (EditText) view.findViewById(R.id.et_pwd_set_pwd);
		et_pwd_set_pwd2 = (EditText) view.findViewById(R.id.et_pwd_set_pwd2);
		btn_pwd_set_confirm = (Button) view.findViewById(R.id.btn_pwd_set_confirm);
		btn_pwd_set_cancel = (Button) view.findViewById(R.id.btn_pwd_set_cancel);
		btn_pwd_set_confirm.setOnClickListener(this);
		btn_pwd_set_cancel.setOnClickListener(this);
		
		setDailog = new AlertDialog.Builder(this)
						.setView(view)
						.show();
	}

	@Override
	public void onClick(View v) {
		if(v==btn_pwd_set_confirm) {//设置的确定
			//得到密码和确认密码
			String pwd = et_pwd_set_pwd.getText().toString();
			String pwd2 = et_pwd_set_pwd2.getText().toString();
			//检查密码不能为空
			if("".equals(pwd.trim())) {
				MSUtils.showMsg(this, "密码必须指定");
				return;
			}
			//检查确认密码必须跟密码相同
			if(!pwd.equals(pwd2)) {
				MSUtils.showMsg(this, "再次密码必须相同");
				return;
			}
			
			//保存密码
			//sp.edit().putString("password", MSUtils.md5(pwd)).commit();
			
			SpUtils.getInstance(this).save(SpUtils.PASSWORD, MSUtils.md5(pwd));
			//移除dialog
			setDailog.dismiss();
			
		} else if(v==btn_pwd_set_cancel) {//设置的取消
			setDailog.dismiss();
		} else if(v==btn_pwd_login_cancel) {//登陆的取消
			loginDailog.dismiss();
		}else if(v==btn_pwd_login_confirm) {//登陆的确定
			//得到输入的密码
			String pwd = MSUtils.md5(et_pwd_login_pwd.getText().toString());
			//得到保存密码
			String savedPwd = v.getTag().toString();
			//比较两个密码是否相同?
			//如果不同, 显示文本小提示
			if(!pwd.equals(savedPwd)) {
				MSUtils.showMsg(this, "密码错误, 请重新输入!");
			} else {
				//相同, 进入设置流程 移除dialog
				loginDailog.dismiss();
				
				MSUtils.showMsg(this, "进入防盗设置的流程!");
				//进入防盗设置的流程!
				toProtected();
			}
		}
	}
	
	/**
	 * 进入防盗设置的流程!
	 */
	private void toProtected() {
		//从sp中根据configed读取对应的值(判断是否已经)
		boolean configed = SpUtils.getInstance(this).get(SpUtils.CONFIGED, false);
		//如果没有(false), 进入设置向导1
		if(!configed){
			startActivity(new Intent(this, Guide1Activity.class));
		} else {
			//如果有(true), 进入防盗信息界面
			startActivity(new Intent(this, ProtectedInfoActivity.class));
		}
	}


	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			exit = false;//使2s之前点击back失效, 需要再点击两次才退出
		}
	};
	private boolean exit = false;//代表是否退出
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK) {//点击的是back
			if(!exit) {
				MSUtils.showMsg(this, "再按一次退出应用!");
				exit = true;
				handler.sendEmptyMessageDelayed(1, 2000);
				return true;//不退出
			} 
		}
		
		return super.onKeyUp(keyCode, event);//默认点back就退出
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);//移除未处理的消息, 防止内存泄露
	}
}
