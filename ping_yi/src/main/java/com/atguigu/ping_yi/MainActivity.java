package com.atguigu.ping_yi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    private MyImageView iv_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_main = (MyImageView)findViewById(R.id.iv_main);
    }

    public void scrollLeft(View v) {
        iv_main.scrollTo(iv_main.getScrollX() + 10, iv_main.getScrollY());
        iv_main.scrollBy(10, 0);
    }

    public void scrollRight(View v) {
        iv_main.scrollTo(iv_main.getScrollX() - 10, iv_main.getScrollY());
        iv_main.scrollBy(-10, 0);
    }

    public void scrollUp(View v) {
        iv_main.scrollBy(0, 10);
        iv_main.scrollTo(iv_main.getScrollX(), iv_main.getScrollY() + 10);
    }

    public void scrollDown(View v) {
        iv_main.scrollBy(0, -10);
        iv_main.scrollTo(iv_main.getScrollX(), iv_main.getScrollY() - 10);
    }

    public void reset1(View v) {
        iv_main.scrollBy(-iv_main.getScrollX(), -iv_main.getScrollY());
        iv_main.scrollTo(0, 0);
    }

    public void reset2(View v) {
        iv_main.smoothRest();
    }
}
