package com.atguigu.slidemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by admin on 2015/12/30.
 * 1. 正常的初始化显示
 *	1). 重写onFinishInflate(): 得到菜单视图
 *  2). 重写onMeasure(): 得到菜单视图的宽高
 *	3). 重写OnLayout(): 对菜单进行重新布局
 * 2. 拖动菜单
 *  2.1). 重写onTouchEvent(): 响应用户的操作
 *  2.2). 在move时计算事件的偏移, 对当前布局进行滚动
 *  2.3). 在up时, 根据布局决的偏移量, 来判断是打开/关闭菜单
 *  2.4). 使用Scoller实现平滑打开/关闭
 */

public class SlideLayout extends FrameLayout {

    private Scroller scroller;
    private View menuView;
    private int menuViewWithd, menuViewHight;
    private boolean isopen = false;
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

    //2.1). 重写onTouchEvent(): 响应用户的操作
    //2.2). 在move时计算事件的偏移, 对当前布局进行滚动
    //2.3). 在up时, 根据布局决的偏移量, 来判断是打开/关闭菜单
    //2.4). 使用Scoller实现平滑打开/关闭

    private int lastX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventX = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                lastX = eventX;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = eventX - lastX;
                int scollX = getScrollX() - dx;
                if(scollX <= -menuViewWithd) {
                    scollX = -menuViewWithd;
                } else if (scollX > 0) {
                    scollX = 0;
                }
                scrollTo(scollX, getScrollY());
                lastX = eventX;
                break;
            case MotionEvent.ACTION_UP:
                int totalScollX = getScrollX();
                if(totalScollX <= -menuViewWithd/2){
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
        isopen = false;
    }

    private void open() {
        scroller.startScroll(getScrollX(), getScrollY(), -menuViewWithd-getScrollX(), -getScrollY());
        invalidate();
        isopen = true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    public void switchState() {
         if(isopen) {
             close();
         } else {
             open();
         }
    }
}
