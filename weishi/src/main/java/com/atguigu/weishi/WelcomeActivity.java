package com.atguigu.weishi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atguigu.weishi.api.ApiClient;
import com.atguigu.weishi.bean.UpdateInfo;
import com.atguigu.weishi.util.MsUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class WelcomeActivity extends Activity {

    private static final int WHAT_TO_MAIN = 5;
    private static final int WHAT_REQUEST_UPDATE_SUCCESS = 1;
    private static final int WHAT_REQUEST_UPDATE_ERROR = 2;
    private static final int WHAT_DOWNLOAD_APK_SUCCESS = 3;
    private static final int WHAT_DOWNLOAD_APK_ERROR = 4;
    private long startTime;
    private LinearLayout ll_welcom_activity;
    private TextView tv_version;
    private String version;
    private UpdateInfo updateInfo;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case WHAT_REQUEST_UPDATE_SUCCESS:
                    if(version.equals(updateInfo.getVersion())) {
                        MsUtils.showMsg(getApplicationContext(), "当前是最新版本");
                        toMainUI();
                    } else {
                        showHintDownloadDialog();
                    }
                    break;
                case WHAT_REQUEST_UPDATE_ERROR:
                    MsUtils.showMsg(getApplicationContext(), "获取最新版本信息失败");
                    toMainUI();
                    break;
                case WHAT_DOWNLOAD_APK_SUCCESS:
                    pd.dismiss();
                    installAPK();
                    break;
                case WHAT_DOWNLOAD_APK_ERROR:
                    pd.dismiss();
                    MsUtils.showMsg(getApplicationContext(), "下载apk失败");
                    toMainUI();
                    break;
                case WHAT_TO_MAIN:
                    finish();
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    break;

            }
        }
    };

    private void installAPK() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
        startActivity(intent);
    }

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

    private ProgressDialog pd;
    private File apkFile;
    private void downloadApk() {
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        apkFile = new File(getExternalFilesDir(null), "update.apk");
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
        setContentView(R.layout.activity_welcome);

        startTime = System.currentTimeMillis();

        ll_welcom_activity = (LinearLayout) findViewById(R.id.ll_welcom_activity);
        tv_version = (TextView) findViewById(R.id.tv_version);

        showAnimation();

        version = MsUtils.getVersion(this);
        tv_version.setText("当前版本：" + version);

        copyDatabases();

        checkVersionUpdate();
    }

    private void checkVersionUpdate() {
        boolean connect = MsUtils.isconnect(this);
        if(!connect) {
            MsUtils.showMsg(this, "手机没有联网");
            toMainUI();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateInfo = ApiClient.getUpdateInfo();
                    handler.sendEmptyMessage(WHAT_REQUEST_UPDATE_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(WHAT_REQUEST_UPDATE_ERROR);
                }
            }
        }).start();
    }

    private void toMainUI() {
        long duration = System.currentTimeMillis() - startTime;
        long dedelayTime = 3000 - duration;
        handler.sendEmptyMessageDelayed(WHAT_TO_MAIN, dedelayTime);
    }

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

    private void copyDatabase(String fileName) {
        File file = new File(getFilesDir(), fileName);
        if(file.exists()) {
            return;
        }
        AssetManager manager = getAssets();
        try {
            InputStream is = manager.open(fileName);
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] buffer = new byte[2048];
            int len;
            while((len = is.read(buffer))!=-1){
                fos.write(buffer,0,len);
            }
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
