package com.atguigu.weishi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.weishi.bean.AppInfo;
import com.atguigu.weishi.util.MsUtils;

import java.util.List;
import java.util.Map;

public class AppManagerActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView lv_app;
    private List<AppInfo> userInfos;
    private List<AppInfo> systemInfos;
    private AppAdapter adapter;
    private LinearLayout ll_app_loading;
    private TextView tv_app_count;
    private BroadcastReceiver receiver;
    private AbsListView.OnScrollListener listener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        lv_app = (ListView)findViewById(R.id.lv_app);
        ll_app_loading = (LinearLayout)findViewById(R.id.ll_app_loading);
        tv_app_count = (TextView)findViewById(R.id.tv_app_count);

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                ll_app_loading.setVisibility(View.VISIBLE);
                tv_app_count.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                Map<Boolean, List<AppInfo>> map = MsUtils.getAllAppInfos(AppManagerActivity.this);
                userInfos = map.get(false);
                systemInfos = map.get(true);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                tv_app_count.setVisibility(View.VISIBLE);
                ll_app_loading.setVisibility(View.GONE);
                tv_app_count.setText("用户应用：" + userInfos.size());
                adapter = new AppAdapter();
                lv_app.setAdapter(adapter);
            }
        }.execute();

        lv_app.setOnItemClickListener(this);
        lv_app.setOnScrollListener(listener);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class AppAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
