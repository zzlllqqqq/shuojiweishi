package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.shoujiweishi.util.MSUtils;

import java.lang.reflect.Method;
import java.util.List;

public class CacheClearActivity extends Activity {

    private static final int WHAT_SHOW_CACHE = 1;
    private TextView tv_cache_clean_status;
    private ProgressBar pb_cache_clean;
    private LinearLayout ll_cache_clean_container;
    private PackageManager pm;
    private long totalCacheSize;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 1) {
                final CacheInfo info = (CacheInfo) msg.obj;
                tv_cache_clean_status.setText("正在扫描" + info.appName);
                pb_cache_clean.incrementProgressBy(1);
                //显示信息视图
                //1. 加载对应的布局文件-->视图
                View view = View.inflate(CacheClearActivity.this, R.layout.item_cache_clear, null);
                //2. 得到用于显示数据的子View
                ImageView iv_clear_icon = (ImageView) view.findViewById(R.id.iv_clear_icon);
                ImageView iv_clear_delete = (ImageView) view.findViewById(R.id.iv_clear_delete);
                TextView tv_clear_name = (TextView) view.findViewById(R.id.tv_clear_name);
                TextView tv_clear_cache = (TextView) view.findViewById(R.id.tv_clear_cache);
                //3. 设置数据, 设置点击监听
                iv_clear_icon.setImageDrawable(info.icon);
                tv_clear_cache.setText(MSUtils.formatSize(CacheClearActivity.this, info.cacheSize));
                tv_clear_name.setText(info.appName);
                iv_clear_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //进入应用详情界面
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.parse("package:" + info.packageName));
                        startActivity(intent);
                    }
                });
                //4. 将视图添加到容器视图中
                ll_cache_clean_container.addView(view, 0);
                totalCacheSize += info.cacheSize;
                if(pb_cache_clean.getProgress() == pb_cache_clean.getMax()) {
                    pb_cache_clean.setVisibility(View.GONE);
                    tv_cache_clean_status.setText("共扫描到"+pb_cache_clean.getMax()+"项缓存数据,总大小"+
                            MSUtils.formatSize(CacheClearActivity.this, totalCacheSize));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);

        pm = getPackageManager();
        tv_cache_clean_status = (TextView)findViewById(R.id.tv_cache_clean_status);
        pb_cache_clean = (ProgressBar)findViewById(R.id.pb_cache_clean);
        ll_cache_clean_container = (LinearLayout)findViewById(R.id.ll_cache_clean_container);
        tv_cache_clean_status.setText("准备开始扫描...");
        new Thread(){
            public void run(){
                //得到所有应用信息
                List<ApplicationInfo> applications = pm.getInstalledApplications(0);
                //设置进度条最大值
                pb_cache_clean.setMax(applications.size());
                for (int i = 0; i < applications.size(); i++){
                    SystemClock.sleep(30);
                    ApplicationInfo applicationInfo = applications.get(i);
                    CacheInfo info = new CacheInfo();
                    Drawable icon = applicationInfo.loadIcon(pm);
                    info.icon = icon;
                    String appName = applicationInfo.loadLabel(pm).toString();
                    info.appName = appName;
                    String packageName = applicationInfo.packageName;
                    info.packageName = packageName;
                    //得到cacheSize
                    getCacheSize(info);
                }

            }
        }.start();
    }

    /**
     * 读取缓存大小
     * @param info
     */
    private void getCacheSize(final CacheInfo info) {
        //反射调用PackageManager.getPackageSizeInfo(String packageName, IPackageStatsObserver observer)
        try {
            Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(pm, info.packageName, new IPackageStatsObserver.Stub(){
                //当获取缓存信息完成时调用
                @Override
                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                    long cacheSize = pStats.cacheSize;
                    info.cacheSize = cacheSize;
                    //使用handler发消息来更新界面
                    Message msg = Message.obtain();
                    msg.what = WHAT_SHOW_CACHE;
                    msg.obj = info;
                    handler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CacheInfo {
        public Drawable icon;
        public String appName;
        public String packageName;
        public long cacheSize;
    }
}
