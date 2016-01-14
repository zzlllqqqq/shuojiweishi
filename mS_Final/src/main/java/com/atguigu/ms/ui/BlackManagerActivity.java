package com.atguigu.ms.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.BlackNumber;
import com.atguigu.ms.dao.BlackNumberDao;

/**
 * 黑名单管理界面
 * @author 张晓飞
 *
 */
public class BlackManagerActivity extends ListActivity {

	private BlackNumberAdapter adapter;
	private List<BlackNumber> data;
	private BlackNumberDao dao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_manager);
		
		adapter = new BlackNumberAdapter();
		dao = new BlackNumberDao(this);
		data = dao.getAll();
		
		//显示列表
		setListAdapter(adapter);
		
		//给listVIew设置产生ContextMenu的监听
		getListView().setOnCreateContextMenuListener(this);
		
		//判断是否是点击骚扰电话通知过来
		String number = getIntent().getStringExtra("number");
		if(number!=null) {
			showAddDialog(number);
		}
		
	}
	
	private void showAddDialog(String number) {
		final EditText editText = new EditText(this);
		if(number!=null) {
			editText.setText(number);
		} else {
			editText.setHint("输入黑名单");
		}
		new AlertDialog.Builder(this)
				.setTitle("添加黑名单")
				.setView(editText)
				.setPositiveButton("添加", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String number = editText.getText().toString();
						
						//更新保存的数据
						BlackNumber blackNumber = new BlackNumber(-1, number);
						dao.add(blackNumber);
						
						//更新内存数据
						data.add(0, blackNumber);
						
						//更新界面
						adapter.notifyDataSetChanged();
					}})
				.setNegativeButton("取消", null)
				.show();
	}

	private int position;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, 1, 0, "更新");
		menu.add(0, 2, 0, "删除");
		//得到下按的position并保存
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		position = info.position;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		
		switch (item.getItemId()) {
		case 1: //更新
			showUpdateDialog();
			break;
		case 2://删除
			//存储
			dao.deleteById(data.get(position).getId());
			//内存
			data.remove(position);
			//界面
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
		
		
		return super.onContextItemSelected(item);
	}
	
	/**
	 * 显示更新的dialog
	 */
	private void showUpdateDialog() {
		final EditText editText = new EditText(this);
		editText.setHint(data.get(position).getNumber());
		new AlertDialog.Builder(this)
				.setTitle("修改黑名单")
				.setView(editText)
				.setPositiveButton("修改", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String number = editText.getText().toString();
						BlackNumber blackNumber = data.get(position);
						//更新内存数据
						blackNumber.setNumber(number);
						
						//更新保存的数据
						dao.update(blackNumber);
						
						//更新界面
						adapter.notifyDataSetChanged();
					}})
				.setNegativeButton("取消", null)
				.show();
	}

	/*
	 * 1. 更新内存数据
	 * 2. 更新保存的数据
	 * 3. 更新界面
	 */
	public void addBlackNumber(View v) {
		showAddDialog(null);
	}
	
	class BlackNumberAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null) {
				convertView = View.inflate(BlackManagerActivity.this, android.R.layout.simple_list_item_1, null);
			}
			
			BlackNumber blackNumber = data.get(position);
			TextView textView = (TextView) convertView;
			textView.setText(blackNumber.getNumber());
			
			return convertView;
		}
		
	}
}
