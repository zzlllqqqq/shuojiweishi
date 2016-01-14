package com.atguigu.quickindex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by admin on 2016/1/4.
 *   1. 初始化显示字母
 *  1). 重写onMeasure(): 得到item的宽高
 *  2). 重写onDraw(): 绘制字母(计算坐标)
 * 2. 操作字母变化
 *  1). 重写onTouchEvent(): 监视用户的操作
 *  2). 在down/move时, 将操作所在的字母变灰色, 其它为白色
 *  3). 在up时, 所有字母都变白色
 * 3. 操作字母时显示对应的提示字母
 *  1). 使用自定义监听器
 *      定义监听器:
 *          定义接口及其回调方法
 *          定义成员变量
 *          定义对应的set方法
 *          在事件发生时, 调用监听器成员的回调方法
 *      使用监听器
 *          给视图对象设置自定义监听器对象
 *          实现回调方法
 *  2). 延迟消息
 *      发延迟消息
 *      移除未处理的消息
 */

public class QuickIndexView extends View {

    private Paint paint;
    private float itemWidth, itemHeight;
    private String[] words = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

    public QuickIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        itemHeight = getMeasuredHeight()/26;
        itemWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < words.length; i++){

            String word = words[i];
            //得到字母宽高
            Rect bounds = new Rect();
            paint.getTextBounds(word, 0, word.length(), bounds);
            int wordWidth = bounds.width();
            int wordHeight = bounds.height();

            //放在wordWidth上面不能用这个值
            if(i == currentIndex) {
                paint.setColor(Color.GRAY);
                //paint.setTextSize(40);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(itemWidth / 2, itemHeight / 2 + itemHeight * i, wordHeight, paint);
            } else {
                paint.setColor(Color.WHITE);
                paint.setTextSize(30);
            }
            //放在最后圆圈显示的位置在选中位置的下一个

            //得到左下角的坐标
            float wordX = itemWidth/2 - wordWidth/2;
            float wordY = itemHeight/2 + wordHeight/2 + i*itemHeight;
            //绘制字母
            canvas.drawText(word, wordX, wordY, paint);

        }
    }

    private int currentIndex = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
            case MotionEvent.ACTION_MOVE :
                //得到事件的Y坐标
                float eventY = event.getY();
                int index = (int) (eventY/itemHeight);
                //只有当index发生改变时强制重绘
                if(currentIndex != index) {
                    currentIndex = index;
                    invalidate();
                    //当事件发生时调用监听器的回调方法
                    if(onIndexChangeListener != null) {
                        onIndexChangeListener.OnIndexChange(words[index]);
                    }
                }
                break;
            case MotionEvent.ACTION_UP :
                currentIndex = -1;
                invalidate();
                if(onIndexChangeListener != null) {
                    onIndexChangeListener.OnUp();
                }
                break;
        }
        return true;
    }

    /**
     *  1). 使用自定义监听器
     *      定义监听器:
     *          定义接口及其回调方法
     *          定义成员变量
     *          定义对应的set方法
     *          在事件发生时, 调用监听器成员的回调方法
     */
    private OnIndexChangeListener onIndexChangeListener;

    public void setOnIndexChangeListener(OnIndexChangeListener onIndexChangeListener) {
        this.onIndexChangeListener = onIndexChangeListener;
    }

    public interface OnIndexChangeListener{

        /**
         * 方法参数: 由View传给Activity
         * 返回值: 由Activity传给View
         */
        public void OnIndexChange (String word);

        /**
         * 当up时调用
         */
        public void OnUp();
    }
}
