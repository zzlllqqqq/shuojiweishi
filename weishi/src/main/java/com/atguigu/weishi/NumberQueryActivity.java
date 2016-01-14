package com.atguigu.weishi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.atguigu.weishi.dao.CommonNumberDao;

import java.util.List;

public class NumberQueryActivity extends Activity {

    private NumberAdapter adapter;
    private ExpandableListView elv_list;
    private List<String> groupData;
    private List<List<String>> chlidData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_query);
        elv_list = (ExpandableListView) findViewById(R.id.elv_list);
        groupData = CommonNumberDao.getGroupList(this);
        chlidData = CommonNumberDao.getChildList(this);
        adapter = new NumberAdapter();
        elv_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String num = chlidData.get(groupPosition).get(childPosition).split("_")[1];
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + num));
                startActivity(intent);
                return false;
            }
        });
        elv_list.setAdapter(adapter);
    }

    class NumberAdapter extends BaseExpandableListAdapter{
        @Override
        public int getGroupCount() {
            return groupData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return chlidData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupData.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return chlidData.get(groupPosition).get(childPosition);
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

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = View.inflate(NumberQueryActivity.this, R.layout.item_expendable_group, null);
            }
            String name = groupData.get(groupPosition);
            TextView textView = (TextView) convertView.findViewById(R.id.tv_item_expandable_group);
            textView.setText(name);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = View.inflate(NumberQueryActivity.this, R.layout.item_expendable_children, null);
            }
            String name_num = (String) getChild(groupPosition, childPosition);
            String[] str = name_num.split("_");
            String name = str[0];
            String num = str[1];
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_item_expandable_child_name);
            TextView tv_num = (TextView) convertView.findViewById(R.id.tv_item_expandable_child_num);
            tv_name.setText(name);
            tv_num.setText(num);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
