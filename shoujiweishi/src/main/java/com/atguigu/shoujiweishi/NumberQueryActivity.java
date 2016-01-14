package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.atguigu.shoujiweishi.dao.CommonNumberDao;

import java.util.List;

public class NumberQueryActivity extends Activity {

    private ExpandableListView elv_list;
    private List<String> groupData;
    private List<List<String>> childData;
    private NumberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_query);

        elv_list = (ExpandableListView)findViewById(R.id.elv_list);
        groupData = CommonNumberDao.getGroupList(this);
        childData = CommonNumberDao.getChildList(this);
        adapter = new NumberAdapter();
        elv_list.setAdapter(adapter);

        elv_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String number = childData.get(groupPosition).get(childPosition).split("_")[1];
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
                return true;
            }
        });

    }

    class NumberAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return groupData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childData.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupData.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
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

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = View.inflate(NumberQueryActivity.this, R.layout.item_expandable_group, null);
            }
                String name = groupData.get(groupPosition);
                TextView textView = (TextView) convertView;
                textView.setText(name);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = View.inflate(NumberQueryActivity.this, R.layout.item_expandable_children, null);
            }
            String name_number = childData.get(groupPosition).get(childPosition);
            String name = name_number.split("_")[0];
            String number = name_number.split("_")[1];
            TextView nameTV = (TextView) convertView.findViewById(R.id.tv_item_expandable_child_name);
            TextView numberTV = (TextView) convertView.findViewById(R.id.tv_item_expandable_child_num);
            nameTV.setText(name);
            numberTV.setText(number);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
