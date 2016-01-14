package com.atguigu.weishi;

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

import com.atguigu.weishi.bean.BlackNumber;
import com.atguigu.weishi.dao.BlackNumberDao;
import com.atguigu.weishi.util.MsUtils;

import java.util.List;

public class BlackNumberActivity extends ListActivity {

    private ListView listView;
    private BlackNumberDao blackNumberDao;
    private List<BlackNumber> data;
    private BlackNumberAdapter adapter;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);

        listView = getListView();
        blackNumberDao = new BlackNumberDao(this);
        data = blackNumberDao.getAll();
        adapter = new BlackNumberAdapter();
        listView.setAdapter(adapter);

        listView.setOnCreateContextMenuListener(this);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final BlackNumber blackNumber = data.get(position);
        switch (item.getItemId()){
            case 1:
                final EditText editText = new EditText(this);
                editText.setHint(blackNumber.getNumber());
                new AlertDialog.Builder(this)
                            .setTitle("修改黑名单号")
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
            case 2:
                data.remove(position);
                blackNumberDao.delete(blackNumber.getId());
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 1, 0, "更新");
        menu.add(0, 2, 0, "删除");
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        position = info.position;
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public void addBlackNumber(View v) {
        showAddDialog(null);
    }

    private void showAddDialog(String number) {
        final EditText editText = new EditText(this);
        if (number == null) {
            editText.setHint("请输入黑名单号码");
        } else {
            editText.setText(number);
        }
        new AlertDialog.Builder(this)
                .setTitle("添加黑名单")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String number = editText.getText().toString().trim();
                        boolean black = blackNumberDao.isBlack(number);
                        if (black) {
                            MsUtils.showMsg(BlackNumberActivity.this, "已有该号码");
                            return;
                        }
                        BlackNumber blackNumber = new BlackNumber(-1, number);
                        int id = blackNumberDao.add(blackNumber);
                        blackNumber.setId(id);
                        data.add(0, blackNumber);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
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
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(BlackNumberActivity.this, android.R.layout.simple_list_item_1, null);
                holder.textView = (TextView) convertView;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            BlackNumber blackNumber = data.get(position);
            holder.textView.setText(blackNumber.getNumber());
            return convertView;
        }
    }

    class ViewHolder {
        TextView textView;
    }
}
