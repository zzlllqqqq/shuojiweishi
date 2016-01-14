package com.atguigu.ce_hua_caidan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by admin on 2016/1/7.
 */
public class SlideLayout extends FrameLayout {

    private Scroller scroller;
    private View menuView;
    private int menuViewWithd, menuViewHight;
    private boolean isOpen;


    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menuView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        menuViewWithd = menuView.getMeasuredWidth();
        menuViewHight = menuView.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        menuView.layout(-menuViewWithd, 0, 0, menuViewHight);
    }

    private int lastX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventX = (int) event.getX();
        switch (event.getAction()) {
            case  MotionEvent.ACTION_DOWN :
                lastX = eventX;
                break;
            case MotionEvent.ACTION_MOVE :
                int dx = eventX - lastX;
                int scrollX = getScrollX() - dx;
                if(scrollX <= -menuViewWithd) {
                    scrollX = -menuViewWithd;
                } else if (scrollX >= 0) {
                    scrollX = 0;
                }
                scrollTo(scrollX, getScrollY());
                lastX = eventX;
                break;
            case MotionEvent.ACTION_UP :
                int totalX = getScrollX();
                if(totalX < -menuViewWithd/2) {
                    open();
                } else {
                    close();
                }
                break;
        }
        return true;
    }

    private void close() {
        scroller.startScroll(getScrollX(), getScrollY(), -getScrollX(), -getScrollY());
        invalidate();
        isOpen = false;
    }

    private void open() {
        scroller.startScroll(getScrollX(), getScrollY(), -menuViewWithd - getScrollX(), -getScrollY());
        invalidate();
        isOpen = true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    public void changState(){
        if(isOpen) {
            close();
        } else {
            open();
        }
    }
}
