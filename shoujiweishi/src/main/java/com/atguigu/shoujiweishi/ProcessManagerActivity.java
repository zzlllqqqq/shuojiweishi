package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.shoujiweishi.bean.ProcessInfo;
import com.atguigu.shoujiweishi.util.MSUtils;

import java.util.ArrayList;
import java.util.List;

public class ProcessManagerActivity extends Activity implements AdapterView.OnItemClickListener {

    private TextView tv_run_process_count;
    private TextView tv_avail_ram;
    private ListView lv_taskmanager;
    private LinearLayout ll_loading;
    private TextView tv_process_count;
    private ProcessAdapter adapter;
    private List<ProcessInfo> userInfos = new ArrayList<>();
    private List<ProcessInfo> systemInfos = new ArrayList<>();
    private ActivityManager activityManager;
    private long availMemSize;
    private long totalMemSize;
    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (userInfos == null) {
                return;
            }
            if (firstVisibleItem >= userInfos.size() + 1) {
                tv_process_count.setText("系统进程：" + systemInfos.size());
            } else {
                tv_process_count.setText("用户进程：" + userInfos.size());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        tv_run_process_count = (TextView) findViewById(R.id.tv_run_process_count);
        tv_avail_ram = (TextView) findViewById(R.id.tv_avail_ram);
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        lv_taskmanager = (ListView) findViewById(R.id.lv_taskmanager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                ll_loading.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                MSUtils.getAllProcessInfos(ProcessManagerActivity.this, systemInfos, userInfos);
                availMemSize = MSUtils.getAvailMem(ProcessManagerActivity.this);
                totalMemSize = MSUtils.getTotalMem(ProcessManagerActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ll_loading.setVisibility(View.GONE);
                tv_run_process_count.setText("进程数：" + (userInfos.size() + systemInfos.size()));
                tv_avail_ram.setText("剩余/总内存：" + MSUtils.formatSize(ProcessManagerActivity.this, availMemSize)
                        + "/" + MSUtils.formatSize(ProcessManagerActivity.this, totalMemSize));
                tv_process_count.setText("用户进程：" + userInfos.size());
                adapter = new ProcessAdapter();
                lv_taskmanager.setAdapter(adapter);
            }
        }.execute();

        //给listView设置监听
        lv_taskmanager.setOnItemClickListener(this);
        //设置滚动监听
        lv_taskmanager.setOnScrollListener(scrollListener);
    }

    public void selectAll(View v) {
        for (ProcessInfo info : userInfos) {
            info.setChecked(true);
        }
        for (ProcessInfo info : systemInfos) {
            info.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    public void unSelect(View v) {
        for (ProcessInfo info : userInfos) {
            info.setChecked(!info.isChecked());
        }
        for (ProcessInfo info : systemInfos) {
            info.setChecked(!info.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    public void killAll(View v) {
        int killCount = 0;
        long freeMemSize = 0;

        for (int i = 0; i < userInfos.size(); i++) {
            ProcessInfo processInfo = userInfos.get(i);
            if (processInfo.isChecked()) {
                activityManager.killBackgroundProcesses(processInfo.getPackageName());
                userInfos.remove(i--);
                killCount++;
                freeMemSize += processInfo.getMemInfoSize();
            }
        }

        for (int i = 0; i < systemInfos.size(); i++) {
            ProcessInfo processInfo = systemInfos.get(i);
            if (processInfo.isChecked()) {
                activityManager.killBackgroundProcesses(processInfo.getPackageName());
                systemInfos.remove(i--);
                killCount++;
                freeMemSize += processInfo.getMemInfoSize();
            }
        }

        availMemSize += freeMemSize;
        tv_run_process_count.setText("进程数：" + (userInfos.size() + systemInfos.size()));
        tv_avail_ram.setText("剩余/总内存：" + MSUtils.formatSize(ProcessManagerActivity.this, availMemSize)
                + "/" + MSUtils.formatSize(ProcessManagerActivity.this, totalMemSize));
        tv_process_count.setText("用户进程：" + userInfos.size());
        MSUtils.showMsg(this, "清理了" + killCount + "个进程，释放了" + MSUtils.formatSize(ProcessManagerActivity.this, freeMemSize) + "资源");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //判断当是TextView时直接返回
        if (position == 0 || position == userInfos.size() + 1) {
            return;
        }
        //得到CheckBox对象
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_item_process_status);
        checkBox.toggle();
        //得到processInfo对象
        ProcessInfo processInfo = (ProcessInfo) adapter.getItem(position);
        processInfo.setChecked(checkBox.isChecked());
    }

    class ProcessAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userInfos.size() + systemInfos.size();
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return "用户进程：" + userInfos.size();
            } else if (position <= userInfos.size()) {
                return userInfos.get(position - 1);
            } else if (position == userInfos.size() + 1) {
                return "系统进程：" + systemInfos.size();
            } else {
                return systemInfos.get(position - userInfos.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object itemData = getItem(position);
            if (position == 0 || position == userInfos.size() + 1) {
                TextView textView = (TextView) View.inflate(ProcessManagerActivity.this, R.layout.app_count, null);
                textView.setText(itemData.toString());
                return textView;
            }

            ViewHolder holder = null;
            ProcessInfo processInfo = (ProcessInfo) itemData;
            if (convertView == null || convertView instanceof TextView) {
                holder = new ViewHolder();
                convertView = View.inflate(ProcessManagerActivity.this, R.layout.item_process_manger, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_process_logo);
                holder.nameTV = (TextView) convertView.findViewById(R.id.tv_item_process_name);
                holder.memSizeTV = (TextView) convertView.findViewById(R.id.tv_item_process_mem);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_item_process_status);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageView.setImageDrawable(processInfo.getIcon());
            holder.nameTV.setText(processInfo.getAppName());
            holder.memSizeTV.setText(MSUtils.formatSize(ProcessManagerActivity.this, processInfo.getMemInfoSize()));
            holder.checkBox.setChecked(processInfo.isChecked());
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageView;
        TextView nameTV;
        TextView memSizeTV;
        CheckBox checkBox;
    }

}
