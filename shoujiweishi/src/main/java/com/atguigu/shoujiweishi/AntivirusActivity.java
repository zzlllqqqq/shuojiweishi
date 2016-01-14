package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.shoujiweishi.dao.AntivirusDao;
import com.atguigu.shoujiweishi.util.MSUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_antivirus)
public class AntivirusActivity extends Activity {

    @ViewById
    ImageView iv_antivirus_scanning;

    @ViewById
    TextView tv_antivirus_status;

    @ViewById
    ProgressBar pb_antivirus_progress;

    @ViewById
    LinearLayout ll_antivirus_container;

    private PackageManager pm;
    List<AntivirusInfo> list = new ArrayList<>();

    @AfterViews
    void afterViews(){
        pm = getPackageManager();
        showScanAnimation();
        scan();
    }

    private void scan() {
        tv_antivirus_status.setText("开始扫描...");
        startScan();
    }

    @Background void startScan() {
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        pb_antivirus_progress.setMax(packageInfos.size());
        for (PackageInfo infos : packageInfos) {
            SystemClock.sleep(20);
            AntivirusInfo info = new AntivirusInfo();
            info.packageName = infos.packageName;
            info.appName = infos.applicationInfo.loadLabel(pm).toString();
            String signatures = infos.signatures[0].toCharsString();
            String md5 = MSUtils.md5(signatures);
            Log.i("TAG", info.appName + "******" + md5);
            boolean virus = AntivirusDao.isVirus(this, md5);
            info.isVirus = virus;
            updateProgress(info);
        }
        onScanFinish();
    }

    @UiThread void onScanFinish() {
        pb_antivirus_progress.setVisibility(View.INVISIBLE);
        tv_antivirus_status.setText("共发现" + list.size() + "个病毒");
        iv_antivirus_scanning.clearAnimation();
        if(list.size() > 0) {
            new AlertDialog.Builder(this)
                        .setTitle("卸载病毒应用")
                        .setMessage("确定卸载"+list.size()+"个病毒应用吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (AntivirusInfo info : list) {
                                    Intent intent = new Intent(Intent.ACTION_DELETE);
                                    intent.setData(Uri.parse("package:" + info.packageName));
                                    startActivity(intent);
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
        }
    }

    @UiThread void updateProgress(AntivirusInfo info) {
        tv_antivirus_status.setText("扫描" + info.appName);
        pb_antivirus_progress.incrementProgressBy(1);
        TextView textView = new TextView(this);
        if(info.isVirus) {
            textView.setText("发现病毒" + info.appName);
            textView.setTextColor(Color.RED);
            list.add(info);
        } else {
            textView.setText("扫描安全" + info.appName);
            textView.setTextColor(Color.BLACK);
        }
        ll_antivirus_container.addView(textView, 0);
    }

    private void showScanAnimation() {
        RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        iv_antivirus_scanning.startAnimation(animation);
    }

    class AntivirusInfo{
        public String appName;
        public String packageName;
        public boolean isVirus;
    }
}
