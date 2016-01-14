package com.atguigu.ms.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.dao.CommonNumberDao;

/**
 * 常用号码查询界面
 * @author 张晓飞
 *
 */
public class CommonNumberActivity extends Activity {

	private ExpandableListView elv_common_number;
	private CommonNumberAdapter adapter;
	List<String> groupData;//分组列表数据
	List<List<String>> childData; //所有子列表数据
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
		
		groupData = CommonNumberDao.getGroupList(this);
		childData = CommonNumberDao.getChildList(this);
		adapter = new CommonNumberAdapter();
		elv_common_number.setAdapter(adapter);
		
		//给elv_common_number的child Item添加点击事件监听
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				//得到选择的号码
				//String number = childData.get(groupPosition).get(childPosition).split("_")[1];
				TextView textView = (TextView) v.findViewById(R.id.tv_item_expandable_child_num);
				String number = textView.getText().toString();
				
				//启动打电话的界面
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+number));
				startActivity(intent );
				
				return false;
			}
		});
	}
	
	class CommonNumberAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {//得到分组列表的个数
			return groupData.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {//得到某个子列表的个数
			return childData.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {//得到某个分组的数据对象
			return groupData.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {//得到某个子item的数据对象
			return childData.get(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		//返回某个分组的视图对象
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if(convertView==null) {
				convertView = View.inflate(CommonNumberActivity.this, R.layout.item_expandable_group, null);
			}
			
			//得到当前行的数据
			String className = groupData.get(groupPosition);
			//得到视图对象
			TextView textView = (TextView) convertView;
			//设置数据
			textView.setText(className);
			
			return convertView;
		}

		//返回某个子Item的View
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if(convertView==null) {
				convertView = View.inflate(CommonNumberActivity.this, R.layout.item_expandable_children, null);
			}
			
			//得到数据
			String name_number = childData.get(groupPosition).get(childPosition);
			String[] nameNubmer = name_number.split("_");
			//得到视图
			TextView nameTV = (TextView) convertView.findViewById(R.id.tv_item_expandable_child_name);
			TextView numberTV = (TextView) convertView.findViewById(R.id.tv_item_expandable_child_num);
			//设置数据
			nameTV.setText(nameNubmer[0]);
			numberTV.setText(nameNubmer[1]);
			
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true; //只有返回才可选择, 并最终实现点击打电话的效果
		}
		
	}
}
