package com.atguigu.circlebar;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

public class MainActivity extends Activity {

    private CircleBar cb_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cb_main = (CircleBar) findViewById(R.id.cb_main);
    }

    private boolean downing = false;

    public void startdownload(View v) {
        if (downing) {
            return;
        }
        new Thread() {
            public void run() {
                downing = true;
                cb_main.setProgress(0);
                int max = 100;
                cb_main.setMax(max);
                for (int i = 0; i < max; i++) {
                    SystemClock.sleep(20);
                    cb_main.setProgress(cb_main.getProgress() + 1);
                }
                downing = false;
            }
        }.start();
    }
}
