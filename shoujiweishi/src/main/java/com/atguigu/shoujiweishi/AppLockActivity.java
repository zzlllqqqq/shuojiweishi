package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.atguigu.shoujiweishi.adapter.base.CommonBaseAdapter;
import com.atguigu.shoujiweishi.adapter.base.ViewHolder;
import com.atguigu.shoujiweishi.bean.AppInfo;
import com.atguigu.shoujiweishi.dao.AppLockDao;
import com.atguigu.shoujiweishi.service.AppLockService;
import com.atguigu.shoujiweishi.util.MSUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 程序锁界面
 */
public class AppLockActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final int WHAT_SHOW_MESSAGE = 1;
    private ListView lv_lock;
    private LinearLayout ll_lock_loading;
    private List<AppInfo> data = new ArrayList<>();
    private List<String> lockPackageNames;
    private AppLockDao appLockDao;
    //private AppLockAdapter adapter;
    private CommonBaseAdapter<AppInfo> adapter;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
           if(msg.what == 1) {
               ll_lock_loading.setVisibility(View.GONE);
               //adapter = new AppLockAdapter(AppLockActivity.this, data, lockPackageNames);
               adapter = new CommonBaseAdapter<AppInfo>(AppLockActivity.this, data, R.layout.item_app_lock) {
                   @Override
                   public void convert(ViewHolder holder, int position) {
                       AppInfo appInfo = data.get(position);
                       boolean lock = lockPackageNames.contains(appInfo.getPackageName());
                       int lockId = lock ? R.drawable.lock : R.drawable.unlock;
                       holder.setImageDrawable(R.id.iv_item_lock_icon, appInfo.getIcon())
                               .setText(R.id.tv_item_lock_name, appInfo.getAppName())
                               .setImageResource(R.id.iv_item_lock_unlock, lockId)
                               .setTag(R.id.iv_item_lock_unlock, lock);
                   }
               };
               lv_lock.setAdapter(adapter);
           }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        lv_lock = (ListView)findViewById(R.id.lv_lock);
        ll_lock_loading = (LinearLayout)findViewById(R.id.ll_lock_loading);
        appLockDao = new AppLockDao(this);
        lockPackageNames = appLockDao.getAll();
        ll_lock_loading.setVisibility(View.VISIBLE);
        new Thread(){
            public void run(){
                Map<Boolean, List<AppInfo>> map = MSUtils.getAllAppInfos(AppLockActivity.this);
                data.addAll(map.get(false));
                data.addAll(map.get(true));
                handler.sendEmptyMessage(WHAT_SHOW_MESSAGE);
            }
        }.start();

        lv_lock.setOnItemClickListener(this);
    }

    //内存、界面、存储
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo appInfo = data.get(position);
        //过滤当前应用
        if(appInfo.getPackageName().equals(getPackageName())) {
            MSUtils.showMsg(this, "当前应用不能加锁！");
            return;
        }
        //动画
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 40, 0, 0);
        translateAnimation.setDuration(500);
        view.startAnimation(translateAnimation);
        //得到当前的应用状态
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_item_lock_unlock);
        boolean lock = (boolean) imageView.getTag();
        //如果锁定实现解锁
        if(lock) {
            lockPackageNames.remove(appInfo.getPackageName());
            imageView.setTag(false);
            imageView.setImageResource(R.drawable.unlock);
            appLockDao.delet(appInfo.getPackageName());
            //通知Service
            Intent intent = new Intent(this, AppLockService.class);
            intent.putExtra("delet", appInfo.getPackageName());
            startService(intent);
        } else {
            //如果解锁实现加锁
            lockPackageNames.add(appInfo.getPackageName());
            imageView.setTag(true);
            imageView.setImageResource(R.drawable.lock);
            appLockDao.add(appInfo.getPackageName());
            //通知Service
            Intent intent = new Intent(this, AppLockService.class);
            intent.putExtra("add", appInfo.getPackageName());
            startService(intent);
        }
    }
}
