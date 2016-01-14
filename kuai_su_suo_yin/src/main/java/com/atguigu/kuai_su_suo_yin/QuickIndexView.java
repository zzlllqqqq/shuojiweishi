package com.atguigu.kuai_su_suo_yin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by admin on 2016/1/7.
 */
public class QuickIndexView extends View {

    private Paint paint;
    private float itemWidth, itemHeight;
    private String[] words = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };
    private int currentIndex = -1;
    private OnIndexChangeListener onIndexChangeListener;

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
        itemWidth = getMeasuredWidth();
        itemHeight = getMeasuredHeight()/26;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < words.length; i++) {
        String word = words[i];
            if(i == currentIndex) {
                paint.setTextSize(40);
                paint.setColor(Color.GRAY);
            } else {
                paint.setColor(Color.WHITE);
                paint.setTextSize(30);
            }

            Rect bounds = new Rect();
            paint.getTextBounds(word, 0, word.length(), bounds);
            int wordWidth = bounds.width();
            int wordHeight = bounds.height();
            float wordX = itemWidth/2 - wordWidth/2;
            float wordY = itemHeight/2 + wordHeight/2 + i*itemHeight;
            canvas.drawText(word, wordX, wordY, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
            case MotionEvent.ACTION_MOVE :
                int index = (int) (event.getY()/itemHeight);
                if(currentIndex != index) {
                    currentIndex = index;
                    invalidate();
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

    public void setOnIndexChangeListener(OnIndexChangeListener onIndexChangeListener){
        this.onIndexChangeListener = onIndexChangeListener;
    }

    public interface OnIndexChangeListener{
        public void OnIndexChange(String word);

        public void OnUp();
    }
}
