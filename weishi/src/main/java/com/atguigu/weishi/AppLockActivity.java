package com.atguigu.weishi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.atguigu.weishi.bean.AppInfo;
import com.atguigu.weishi.dao.AppLockDao;
import com.atguigu.weishi.util.MsUtils;
import com.example.adapter.base.CommonBaseAdapter;
import com.example.adapter.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppLockActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final int WHAT_SHOW_MESSAGE = 1;
    private ListView lv_lock;
    private LinearLayout ll_lock_loading;
    private AppLockDao appLockDao;
    private List<AppInfo> data = new ArrayList<>();
    private List<String> lockPackageName;
    private CommonBaseAdapter<AppInfo> adapter;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 1) {
                ll_lock_loading.setVisibility(View.GONE);
                adapter = new CommonBaseAdapter<AppInfo>(AppLockActivity.this, data,R.layout.item_app_lock) {
                    @Override
                    public void convert(ViewHolder holder, int position) {
                        AppInfo appInfo = data.get(position);
                        boolean lock = lockPackageName.contains(appInfo.getPackageName());
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
        lockPackageName = appLockDao.getAll();
        ll_lock_loading.setVisibility(View.VISIBLE);
        new Thread(){
            public void run(){
                Map<Boolean, List<AppInfo>> map = MsUtils.getAllAppInfos(AppLockActivity.this);
                data.addAll(map.get(true));
                data.addAll(map.get(false));
                handler.sendEmptyMessage(WHAT_SHOW_MESSAGE);
            }
        }.start();
        lv_lock.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo appInfo = data.get(position);
        if(appInfo.getPackageName().equals(getPackageName())) {
            MsUtils.showMsg(this, "当前应用不能锁定");
            return;
        }

        //*******少写view.就会出现点击不是当前应用被锁
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_item_lock_unlock);
        boolean lock = (boolean) imageView.getTag();

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 40, 0, 0);
        translateAnimation.setDuration(500);
        view.startAnimation(translateAnimation);

        if(lock) {
            lockPackageName.remove(appInfo.getPackageName());
            imageView.setTag(false);
            imageView.setImageResource(R.drawable.unlock);
            appLockDao.delete(appInfo.getPackageName());
        } else {
            lockPackageName.add(appInfo.getPackageName());
            imageView.setTag(true);
            imageView.setImageResource(R.drawable.lock);
            appLockDao.add(appInfo.getPackageName());
        }
    }
}
