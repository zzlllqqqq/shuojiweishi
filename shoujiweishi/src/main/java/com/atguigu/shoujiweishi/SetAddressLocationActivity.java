package com.atguigu.shoujiweishi;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.atguigu.shoujiweishi.util.SPUtils;

public class SetAddressLocationActivity extends Activity implements View.OnTouchListener {

    private LinearLayout ll_location_set;
    private RelativeLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_address_location);

        ll_location_set = (LinearLayout) findViewById(R.id.ll_location_set);
        ll_location_set.setOnTouchListener(this);

        parentLayout = (RelativeLayout) ll_location_set.getParent();
        //更新ll_location_set的背景图片
        int styleIndex = SPUtils.getInstance(this).getValue(SPUtils.STYLE_INDEX, 0);
        int[] bgIds = {R.drawable.call_locate_white, R.drawable.call_locate_orange,
                R.drawable.call_locate_green, R.drawable.call_locate_blue, R.drawable.call_locate_gray};
        ll_location_set.setBackgroundResource(bgIds[styleIndex]);

        //根据保存的位置显示
        int left = SPUtils.getInstance(this).getValue(SPUtils.UPLEFT, -1);
        int top = SPUtils.getInstance(this).getValue(SPUtils.UPTOP, -1);
        if(left == -1) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ll_location_set.getLayoutParams();
            params.leftMargin = left;
            params.topMargin = top;
        }
    }

    private int lastX, lastY, maxRight, maxBottom;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                lastY = eventY;
                if (maxBottom == 0) {
                    maxBottom = parentLayout.getBottom();
                    maxRight = parentLayout.getRight();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = eventX - lastX;
                int dy = eventY - lastY;
                int left = ll_location_set.getLeft() + dx;
                int top = ll_location_set.getTop() + dy;
                int right = ll_location_set.getRight() + dx;
                int bottom = ll_location_set.getBottom() + dy;
                if (left < 0) {
                    right = right - left;
                    left = 0;
                }
                if (top < 0) {
                    bottom = bottom - top;
                    top = 0;
                }
                if (right > maxRight) {
                    left = left - (right - maxRight);
                    right = maxRight;
                }
                if (bottom > maxBottom) {
                    top = top - (bottom - maxBottom);
                    bottom = maxBottom;
                }
                ll_location_set.layout(left, top, right, bottom);
                lastY = eventY;
                lastX = eventX;
                break;
            case MotionEvent.ACTION_UP:
                //保存视图的坐标(左上角)
                int upLeft = ll_location_set.getLeft();
                int upTop = ll_location_set.getTop();
                SPUtils.getInstance(this).save(SPUtils.UPLEFT, upLeft);
                SPUtils.getInstance(this).save(SPUtils.UPTOP, upTop);
                break;
        }
        return true;
    }
}
