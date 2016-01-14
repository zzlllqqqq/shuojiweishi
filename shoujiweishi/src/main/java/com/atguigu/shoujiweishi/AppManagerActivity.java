package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.shoujiweishi.bean.AppInfo;
import com.atguigu.shoujiweishi.util.MSUtils;

import java.util.List;
import java.util.Map;

public class AppManagerActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView lv_app;
    private LinearLayout ll_app_loading;
    private TextView tv_app_count;
    private List<AppInfo> systemInfos;
    private List<AppInfo> userInfos;
    private Appadapter adapter;
    private BroadcastReceiver receiver;
    private AbsListView.OnScrollListener onscrollLister = new AbsListView.OnScrollListener() {

        /**
         * 当滚动状态发生改变时调用
         * SCROLL_STATE_IDLE = 0; //没有动
         * SCROLL_STATE_TOUCH_SCROLL=1 //拖动
         * SCROLL_STATE_FLING=2 //因为惯性快速滑动
         * @param view
         * @param scrollState
         */
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                if(pw != null && pw.isShowing()) {
                    pw.dismiss();
                }
            }
        }

        /**
         * 当拖动滚动时调用(在设置监听时就会调用几次, 而此时没有数据)
         * @param view
         * @param firstVisibleItem 第一个可出的item的position
         * @param visibleItemCount
         * @param totalItemCount
         */
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(userInfos == null) {
                return;
            } else if(firstVisibleItem >= userInfos.size() + 1) {
                tv_app_count.setText("系统应用: " + systemInfos.size());
            } else {
                tv_app_count.setText("用户应用: " + userInfos.size());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        lv_app = (ListView) findViewById(R.id.lv_app);
        ll_app_loading = (LinearLayout) findViewById(R.id.ll_app_loading);
        tv_app_count = (TextView) findViewById(R.id.tv_app_count);


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                ll_app_loading.setVisibility(View.VISIBLE);
                tv_app_count.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                Map<Boolean, List<AppInfo>> map = MSUtils.getAllAppInfos(AppManagerActivity.this);
                systemInfos = map.get(true);
                userInfos = map.get(false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ll_app_loading.setVisibility(View.GONE);
                tv_app_count.setVisibility(View.VISIBLE);
                tv_app_count.setText("用户应用：" + userInfos.size());
                adapter = new Appadapter();
                lv_app.setAdapter(adapter);
            }
        }.execute();

        lv_app.setOnItemClickListener(this);
        //设置ListView的滚动监听
        lv_app.setOnScrollListener(onscrollLister);
        //设置接收器监听更新应用列表
        //注册receiver
        receiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
    }

    private int position;
    private PopupWindow pw;
    private View pwView;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        //排除特别情况
        if(position == 0 || position == userInfos.size() + 1) {
            return;
        }
        if(pw == null) {
            pwView = View.inflate(AppManagerActivity.this, R.layout.pw_view, null);
            pwView.findViewById(R.id.ll_pw_uninstall).setOnClickListener(this);
            pwView.findViewById(R.id.ll_pw_run).setOnClickListener(this);
            pwView.findViewById(R.id.ll_pw_share).setOnClickListener(this);
            //这里用到的是view
            pw = new PopupWindow(pwView, view.getWidth() - 80, view.getHeight());
            //只有这样才能显示动画
            pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        //如果显示那么先移除
        if(pw.isShowing()) {
            pw.dismiss();
        }
        //显示
        pw.showAsDropDown(view, 40, -pw.getHeight());
        //动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1);
        scaleAnimation.setDuration(500);
        pwView.setAnimation(scaleAnimation);
    }

    @Override
    public void onClick(View v) {
        //先移除pw
        pw.dismiss();
        AppInfo appInfo = (AppInfo) adapter.getItem(position);
        switch (v.getId()) {
            case R.id.ll_pw_uninstall :
                uninstallApp(appInfo);
                break;
            case R.id.ll_pw_run :
                startApp(appInfo.getPackageName());
                break;
            case R.id.ll_pw_share :
                shareApp(appInfo.getAppName());
                break;
        }
    }

    /**
     * 分享
     * @param appName
     */
    private void shareApp(String appName) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");// 纯文本
        //intent.putExtra(Intent.EXTRA_SUBJECT, "应用分享");
        intent.putExtra(Intent.EXTRA_TEXT, "分享一个不错的应用: " + appName); // 内容
        startActivity(intent);
    }

    /**
     * 启动指定应用
     * @param packageName
     */
    private void startApp(String packageName) {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            MSUtils.showMsg(this, "此应用无法启动");
        } else {
            startActivity(intent);
        }
    }

    /**
     * 卸载指定应用
     * @param appInfo
     */
    private void uninstallApp(AppInfo appInfo) {
        if (appInfo.isSystem()) {
            Toast.makeText(this, "系统应用不能卸载!", Toast.LENGTH_SHORT).show();
        } else if(getPackageName().equals(appInfo.getPackageName())) {
            Toast.makeText(this, "当前应用不能卸载!", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
            startActivity(intent);
        }
    }

    class Appadapter extends BaseAdapter {

        //返回需要显示的行数
        @Override
        public int getCount() {
            //+2  是指两个TextView的位置
            return userInfos.size() + systemInfos.size() + 2;
        }

        //返回指定position所对应的数据对象(需要显示)
        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return "用户应用：" + userInfos.size();
            } else if (position <= userInfos.size()) {
                return userInfos.get(position - 1);
            } else if (position == userInfos.size() + 1) {
                return "系统应用：" + systemInfos.size();
            } else {
                return systemInfos.get(position - userInfos.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /*
        返回指定position所对应的item视图
        可能是TextView
        也可能是LinearLayout
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //得到当前行的数据
            Object itemData = getItem(position);
            //返回TextView
            if(position == 0 || position == userInfos.size() + 1) {
                TextView textView = (TextView) View.inflate(AppManagerActivity.this, R.layout.app_count, null);
                textView.setText((String) itemData);
                return textView;
            }
            //返回LinearLayout
            ViewHolder holder = null;
            //如果没有或那么在进行拖动时候出现复用会导致TextView和ImageView冲突
            if(convertView == null || convertView instanceof TextView) {//没有缓存的item,缓存的是TextView而不是LinearLayout
                holder = new ViewHolder();
                convertView = View.inflate(AppManagerActivity.this, R.layout.item_app, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_app_icon);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_item_app_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AppInfo appInfo = (AppInfo) itemData;
            holder.imageView.setImageDrawable(appInfo.getIcon());
            holder.textView.setText(appInfo.getAppName());
            return convertView;
        }
    }

    class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }

    class UninstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String dataString = intent.getDataString();
            String packageName = dataString.substring(dataString.indexOf(":") + 1);
            AppInfo appInfo = new AppInfo(packageName, null, null, false);
            userInfos.remove(appInfo);//需要重写appInfo的equal方法
            adapter.notifyDataSetChanged();
        }
    }
}
