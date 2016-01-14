package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atguigu.shoujiweishi.api.ApiClient;
import com.atguigu.shoujiweishi.bean.UpdateInfo;
import com.atguigu.shoujiweishi.util.MSUtils;
import com.atguigu.shoujiweishi.util.SPUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WelcomActivity extends Activity {

    private long startTime;
    private static final int WHAT_REQUEST_UPDATE_SUCCESS = 1;
    private static final int WHAT_REQUEST_UPDATE_ERROR = 2;
    private static final int WHAT_DOWNLOAD_APK_SUCCESS = 3;
    private static final int WHAT_DOWNLOAD_APK_ERROR = 4;
    private static final int WHAT_TO_MAIN = 5;
    private LinearLayout ll_welcom_activity;
    private TextView tv_version;
    private String version;
    private UpdateInfo updateInfo;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_REQUEST_UPDATE_SUCCESS:
                    if (version.equals(updateInfo.getVersion())) {
                        MSUtils.showMsg(getApplicationContext(), "当前是最新版本");
                        toMainUI();
                    } else {
                        showHintDownloadDialog();
                    }
                    break;
                case WHAT_REQUEST_UPDATE_ERROR:
                    MSUtils.showMsg(getApplicationContext(), "获取最新版本信息失败");
                    toMainUI();
                    break;
                case WHAT_DOWNLOAD_APK_SUCCESS:
                    pd.dismiss();
                    installAPK();
                    break;
                case WHAT_DOWNLOAD_APK_ERROR:
                    pd.dismiss();
                    MSUtils.showMsg(getApplicationContext(), "下载apk失败");
                    toMainUI();
                    break;
                case WHAT_TO_MAIN:
                    finish();
                    startActivity(new Intent(WelcomActivity.this, MainActivity.class));
                    break;
            }
        }
    };

    /**
     * 安装APK
     */
    private void installAPK() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
        startActivity(intent);
    }

    /**
     * 显示提示下载的dialog
     */
    private void showHintDownloadDialog() {
        new AlertDialog.Builder(this)
                .setTitle("下载最新版本")
                .setMessage(updateInfo.getDesc())
                .setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadApk();
                    }
                })
                .setNegativeButton("暂不下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toMainUI();
                    }
                })
                .show();
    }

    /**
     * 下载最新apk
     */
    private ProgressDialog pd;
    private File apkFile;
    private void downloadApk() {
        //1. 显示提示视图/准备
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        apkFile = new File(getExternalFilesDir(null), "update.apk");
        //2. 分线程, 联网下载, 过程中提示下载进度
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiClient.downloadAPK(pd, apkFile, updateInfo.getApkUrl());
                    handler.sendEmptyMessage(WHAT_DOWNLOAD_APK_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(WHAT_DOWNLOAD_APK_ERROR);
                }
            }
        }).start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);

        //统计开始时间
        startTime = System.currentTimeMillis();
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ll_welcom_activity = (LinearLayout) findViewById(R.id.ll_welcom_activity);
        tv_version = (TextView) findViewById(R.id.tv_version);

        //显示欢迎动画
        showAnimation();

        //显示版本号
        version = MSUtils.getVersion(this);
        tv_version.setText("当前版本：" + version);

        //将assets下所有数据库文件拷贝到手机内部的files下
        copyDatabases();

        //检查版本更新
        checkVersionUpdate();

        //生成桌面快捷方式
        markShortcut();

    }

    /**
     * 生成桌面快捷方式
     */
    private void markShortcut() {
        boolean shortcut = SPUtils.getInstance(this).getValue(SPUtils.SHORT_CUT, false);
        if(!shortcut) {
            Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            //图标
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.drawable.shortcut));
            //文字
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "TMD");
            //点击的意图
            Intent clickIntent = new Intent("com.atguigu.shoujiweishi.MainActivity.action");
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, clickIntent);
            //发送广播, 桌面(laucher)应用有Receiver接收并生成桌面图标
            sendBroadcast(intent);
            //保存标识
            SPUtils.getInstance(this).save(SPUtils.SHORT_CUT, true);
        }
    }

    /**
     * 检查版本更新
     */
    private void checkVersionUpdate() {
        //检查手机是否联网
        boolean connect = MSUtils.isconnect(this);
        //没网进入主界面
        if (!connect){
            MSUtils.showMsg(this, "手机没有联网");
            toMainUI();
            return;
        }

        //启动分线程, 联网请求, 得到包含最新apk信息的对象
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateInfo = ApiClient.getUpdateInfo();
                    handler.sendEmptyMessage(WHAT_REQUEST_UPDATE_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    //发失败的消息
                    handler.sendEmptyMessage(WHAT_REQUEST_UPDATE_ERROR);
                }
            }
        }).start();
    }

    private void toMainUI() {
        long duration = System.currentTimeMillis() - startTime;
        long delayTime = 3000 - duration;
        handler.sendEmptyMessageDelayed(WHAT_TO_MAIN, delayTime);
    }

    /**
     * 将assets下所有数据库文件拷贝到手机内部的files下
     */
    private void copyDatabases() {
        new Thread(){
            @Override
            public void run() {
                copyDatabase("address.db");
                copyDatabase("antivirus.db");
                copyDatabase("commonnum.db");
            }
        }.start();
    }

    /**
     * 将assets下指定名称的数据库文件拷贝到手机内部的files下
     * @param fileName
     */
    private void copyDatabase(String fileName) {
        //判断Files是否存在此文件, 如果存在结束
        File file = new File(getFilesDir(), fileName);
        if (file.exists()){
            return;
        }
        //得到InputStream
        AssetManager assetManager = getAssets();
        try {
            InputStream is = assetManager.open(fileName);
            //得到outputStream
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            //边读边写
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer))!=-1){
                fos.write(buffer,0,len);
            }
            //关闭
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示欢迎动画
     * ll_welcome_root
     * RotateAnimation
     * ScaleAnimation
     * AlphaAnimation
     * AnimationSet
     */
    private void showAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(2000);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1,0,1, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(2000);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(2000);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        ll_welcom_activity.setAnimation(animationSet);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
