package com.atguigu.ms.ui;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.atguigu.ms.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 显示联系人列表的界面
 * @author 张晓飞
 *
 */
public class ContactListActivity extends ListActivity implements OnItemClickListener {

	private List<Map<String, String>> data = new ArrayList<Map<String,String>>();
	private ContactAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		
		//得到列表数据
			//得到resolver对象
		ContentResolver resolver = getContentResolver();
			//执行查询
		Cursor cursor = resolver.query(Phone.CONTENT_URI, new String[]{Phone.DISPLAY_NAME,Phone.NUMBER}, null, null, null);
		while(cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			//name
			map.put("name", cursor.getString(0));
			//number
			map.put("number", cursor.getString(1));
			data.add(map);
		}
		cursor.close();
		//创建Adapter对象
		adapter = new ContactAdapter();
		//显示列表
		setListAdapter(adapter);

		//给listView设置item的点击监听
		getListView().setOnItemClickListener(this);
	}
	
	class ContactAdapter extends BaseAdapter {

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
			ViewHolder holder = null;
			if(convertView==null) {
				convertView = View.inflate(ContactListActivity.this, R.layout.item_contact, null);
				holder = new ViewHolder();
				holder.nameTV = (TextView) convertView.findViewById(R.id.tv_item_name);
				holder.numberTV = (TextView) convertView.findViewById(R.id.tv_item_number);
				//保存holder
				convertView.setTag(holder);
			} else {
				//从convertView取出tag
				holder = (ViewHolder) convertView.getTag();
			}
			
			Map<String, String> map = data.get(position);
			holder.nameTV.setText(map.get("name"));
			holder.numberTV.setText(map.get("number"));
			
			return convertView;
		}
		
		
		class ViewHolder {
			public TextView nameTV;
			public TextView numberTV;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//得到number
		String number = data.get(position).get("number");
		//设置结果
		Intent data = getIntent();
		data.putExtra("NUMBER", number);
		setResult(RESULT_OK, data);
		//返回
		finish();
	}
}
