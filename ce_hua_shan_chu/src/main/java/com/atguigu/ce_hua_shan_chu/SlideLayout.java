package com.atguigu.ce_hua_shan_chu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by admin on 2016/1/10.
 * 1. 正常的初始显示item
 * 1). 得到子View对象(contentView, menuView) : onFinishInflate()
 * 2). 得到子View的宽高: onMeasure()
 * 3). 对菜单视图进行重新布局: onLayout()
 * 2. 通过手势拖动打开或关闭menu
 * 2.1). 响应用户操作: onTouchEvent() 返回true
 * 2.2). move时计算事件的移动, 对视图进行对应的滚动: scrollTo()
 * 2.3). up时, 得到总的偏移量, 判断是平滑打开/平滑关闭
 * 2.4). 实现平滑打开和关闭: 使用Scroller
 * 3. 使用ListView显示列表:
 * 3.1). 利用CommonBaseAdapter显示列表
 * 3.2). 发现问题:
 * 1).有时不能自动打开和关闭
 * 2).可以打开个多个
 *
 *
 * 事件机制中，事件是由外向内分发的。处理时是由内向外处理的
 *
 *
 * 4. 解决问题1: 有时不能自动打开和关闭
 * 本质: 当前视图(SlideLayout)与父视图(ListView)的事件冲突
 * 原因: 当用户在竖直方向有一定移动量时, ListView拦截了事件,
 * 一旦拦截了事件再也不会传给当前视图(也就无法自动打开和关闭)
 * 解决: 不让父视图拦截(反拦截: 不让父View拦截事件)
 * 什么时候拦截? : 当得知用户是在做水平移动: totalDx>totalDy  & totalDx>10
 * 拦截: getParent().requestDisallowInterceptTouchEvent(true);
 * <p/>
 * 5. 解决当前视图与子视图的事件冲突
 * 解决: 拦截(不让子View消费事件)
 * 如何拦截? : onInterceptTouchEvent() 返回true
 * 什么条件下? : totalDx>8
 * <p/>
 * 6. 解决可以打开多个Item的问题
 * 解决办法: 自定义监听器
 */


//自定义布局一般步骤
public class SlideLayout extends FrameLayout {

    private View contentView, menuView;
    private int contentWidth, menuWidth, viewHight;
    private Scroller scroller;

    //① 构造器，如果子视图要有拖动效果，那么则需要在构造器中初始化一个scoller对象
    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
    }

    //② 通过getChildAt()方法得到子View视图，通过下标得到相应的视图
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        menuView = getChildAt(1);
    }

    //③ 通过onMeasure()方法得到子视图的宽高，若有多个子视图那么久分别调用getMeasuredWidth(Height)方法得到
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        contentWidth = contentView.getMeasuredWidth();
        menuWidth = menuView.getMeasuredWidth();
        viewHight = getMeasuredHeight();
    }

    //④ 重新布局关键是计算出各位置的坐标
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        menuView.layout(contentWidth, 0, contentWidth + menuWidth, viewHight);
    }

    private int lastX, lastY, downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int evenX = (int) event.getRawX();
        int evenY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = lastX = evenX;
                downY = lastY = evenY;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = evenX - lastX;
                int toScrollX = getScrollX() - dx;
                if (toScrollX < 0) {
                    toScrollX = 0;
                } else if (toScrollX > menuWidth) {
                    toScrollX = menuWidth;
                }
                scrollTo(toScrollX, getScrollY());
                lastX = evenX;

                //什么时候拦截?（反拦截） : 当得知用户是在做水平移动: totalDx>totalDy  & totalDx>8
                int totalDx = Math.abs(evenX - downX);
                int totalDy = Math.abs(evenY - downY);
                if (totalDx > totalDy && totalDx > 8) {
                    //让父视图不要拦截事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                int totalScrollX = getScrollX();
                if (totalScrollX < menuWidth / 2) {
                    menuClose();
                } else {
                    menuOpen();
                }
                break;
        }
        return true;
    }

    public void menuOpen() {
        //侧滑菜单移动的距离可以理解为起始位置的坐标加上结束位置的坐标等于滑动菜单的宽（高）
        //从左侧滑出两者相加等于负的菜单宽度，从右侧滑出两者相加等于菜单宽度
        scroller.startScroll(getScrollX(), getScrollY(), menuWidth - getScrollX(), -getScrollY());
        //起始位置均为getScrollX（getscrollY）结束位置的X坐标就是（±）菜单宽度-起始位置，Y坐标就是0或者-getScrollY
        invalidate();
        if (onStateChangeListener != null) {
            onStateChangeListener.onOpen(this);
        }
    }

    public void menuClose() {
        scroller.startScroll(getScrollX(), getScrollY(), -getScrollX(), -getScrollY());
        invalidate();
        if (onStateChangeListener != null) {
            onStateChangeListener.onClose(this);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    //什么条件下? : totalDx>8
    //不让子视图消费事件（拦截）
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        int eventX = (int) ev.getRawX();
        int eventY = (int) ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = lastX = eventX;
                downY = lastY = eventY;
                if(onStateChangeListener != null) {
                    onStateChangeListener.onDown(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int totalX = Math.abs(eventX - downX);
                if(totalX > 8) {
                    intercept = true;
                }
                break;
        }
        return intercept;

    }

    //创建自定义的监听步骤
    //1、创建接口，并在接口中定义回调方法
    //2、创建接口类型的成员变量
    //3、创建改成员变量的set方法
    private OnStateChangeListener onStateChangeListener;

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public interface OnStateChangeListener {
        public void onOpen(SlideLayout slideLayout);

        public void onClose(SlideLayout slideLayout);

        public void onDown(SlideLayout slideLayout);
    }
}
