package com.atguigu.scrolltest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * Created by admin on 2015/12/30.
 */
public class MyImageView extends ImageView {

    private Scroller scroller;
    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        scroller = new Scroller(context);
    }


    public void smoothReset() {
        scroller.startScroll(getScrollX(), getScrollY(), -getScrollX(), -getScrollY());
        invalidate();
    }

    @Override
    public void computeScroll() {
        if(scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }
}
