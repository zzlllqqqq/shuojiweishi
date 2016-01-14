package com.atguigu.shoujiweishi.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.atguigu.shoujiweishi.LockScreenActivity;
import com.atguigu.shoujiweishi.dao.AppLockDao;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序锁服务
 */
public class AppLockService extends Service {
    private boolean flag = true;//当前是否还需要监视
    private List<String> lockPackageNames;
    private List<String> unLockPackageNames = new ArrayList<>();
    private AppLockDao appLockDao;
    private ActivityManager am;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        appLockDao = new AppLockDao(this);
        lockPackageNames = appLockDao.getAll();
        //启动分线程进行监听
        new Thread(){
            public void run(){
                while(flag) {
                //得到当前最顶部的栈所对应的应用包名
                    String topPackageName = getTopPackageName();
                //判断是否是需要锁定的
                    if(lockPackageNames.contains(topPackageName) //在需要锁定的集合中
                            && !unLockPackageNames.contains(topPackageName)) {//不在已经解锁的集合中
                        //如果是, 启动锁定的界面
                        startLockScreenActivity(topPackageName);
                    }
                //休息一会
                    SystemClock.sleep(200);
                }
            }
        }.start();
    }

    /**
     * 启动锁定的界面
     */
    private void startLockScreenActivity(String packageName) {
        Intent intent = new Intent(this, LockScreenActivity.class);
        intent.putExtra("packageName", packageName);
        //在service中启动Activity时必须给他添加一个标志
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 得到当前最顶部的栈所对应的应用包名
     * @return
     */
    private String getTopPackageName() {
        return am.getRunningTasks(1).get(0).topActivity.getPackageName();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
    }

    //每次startSevice()都会调用此方法

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String packageName = intent.getStringExtra("packageName");
        if(packageName!=null) {
            unLockPackageNames.add(packageName);
        }

        String deletName = intent.getStringExtra("delet");
        if(deletName!=null) {
            lockPackageNames.remove(deletName);
        }

        String addName = intent.getStringExtra("add");
        if(addName!=null) {
            lockPackageNames.add(addName);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
