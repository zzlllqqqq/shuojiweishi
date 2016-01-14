package com.atguigu.shoujiweishi;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.shoujiweishi.bean.BlackNumber;
import com.atguigu.shoujiweishi.dao.BlackNumberDao;

import java.util.List;

public class BlackNumberActiity extends ListActivity {

    private ListView listView;
    private BlackNumberDao blackNumberDao;
    private BlackAdapter adapter;
    private List<BlackNumber> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number_actiity);

        listView = getListView();
        blackNumberDao = new BlackNumberDao(this);
        data = blackNumberDao.getAll();
        adapter = new BlackAdapter();

        listView.setAdapter(adapter);
        //setListAdapter(adapter);
        listView.setOnCreateContextMenuListener(this);
        //带返回值，消息提示点击时带回的号码
        String number = getIntent().getStringExtra("number");
        if(number != null) {
            //显示添加对话框
            showAddDialog(number);
        }
    }

    private int postion;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //
        menu.add(0, 1, 0, "更新");
        menu.add(0, 2, 0, "删除");

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        postion = info.position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final BlackNumber blackNumber = data.get(postion);
        switch (item.getItemId()) {
            case 1 :
                final EditText editText = new EditText(this);
                editText.setHint(blackNumber.getNumber());
                new AlertDialog.Builder(this)
                            .setTitle("修改黑名单")
                            .setView(editText)
                            .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String number = editText.getText().toString().trim();
                                    blackNumber.setNumber(number);
                                    blackNumberDao.update(blackNumber);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                break;
            case 2 :
                data.remove(postion);
                blackNumberDao.deletById(blackNumber.getId());
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void addBlackNumber(View v) {
        showAddDialog(null);
    }

    private void showAddDialog(String number) {
        //添加黑名单的dialog
        final EditText editText = new EditText(this);
        if(number == null) {
            editText.setHint("输入黑名单");
        } else {
            editText.setText(number);
        }
        new AlertDialog.Builder(this)
                    .setTitle("添加黑名单")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //分为内存、界面和存储
                            String number = editText.getText().toString().trim();
                            BlackNumber blackNumber = new BlackNumber(-1, number);
                            int id = blackNumberDao.add(blackNumber);
                            blackNumber.setId(id);
                            //新添加的放在最上面
                            data.add(0, blackNumber);
                            //更新界面
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
    }

    class BlackAdapter extends BaseAdapter{

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
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(BlackNumberActiity.this, android.R.layout.simple_list_item_1, null);
                //holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
                //因为这里的convertView只是一个简单的TextView.可以直接将其转换为TextView对象
                holder.textView = (TextView) convertView;
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            //得到当前行数据
            BlackNumber blackNumber = data.get(position);
            //设置数据
            holder.textView.setText(blackNumber.getNumber());
            return convertView;
        }
    }

    class ViewHolder{
        TextView textView;
    }
}
