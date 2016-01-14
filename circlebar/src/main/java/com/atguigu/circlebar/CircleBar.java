package com.atguigu.circlebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by admin on 2015/12/29.
 */
public class CircleBar extends View {

    private int progress; //当前进度
    private int max = 100; //最大进度
    private int roundColor = Color.BLUE; //圆环的颜色
    private int roundProgressColor = Color.RED; //圆环进度的颜色
    private float roundWidth = 10; //圆环的宽度
    private float textSize = 20; //文字的大小
    private int textColor = Color.BLACK; //文字的颜色

    private Paint paint;
    private float viewWidth;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        //强制重绘 否则不能实时更新进度
        //invalidate();//不可以, 不可以在分线程执行
        postInvalidate();

    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getRoundColor() {
        return roundColor;
    }

    public void setRoundColor(int roundColor) {
        this.roundColor = roundColor;
    }

    public int getRoundProgressColor() {
        return roundProgressColor;
    }

    public void setRoundProgressColor(int roundProgressColor) {
        this.roundProgressColor = roundProgressColor;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public CircleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        //只用定义一次画笔，在构造器中完成
        paint = new Paint();
        //去毛边
        paint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //得到自定义视图绘画的范围
        //还有一个Measure方法（两者区别？？？？）
        viewWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //1. 绘制背景圆环
        //圆环半径  内部定义圆环半径为视图宽度一半减去圆环宽度一般
        float radius = viewWidth / 2 - roundWidth / 2;
        //设置画笔
        paint.setColor(roundColor);
        paint.setStrokeWidth(roundWidth);
        //设置画空心圆形的样式是在画笔上定义
        paint.setStyle(Paint.Style.STROKE);
        //画布上画空心圆形   画的动作基本都是在画布上定义
        canvas.drawCircle(viewWidth / 2, viewWidth / 2, radius, paint);
        //2. 绘制进度圆弧
        //确定以哪个点为视图原点
        RectF rectF = new RectF(roundWidth / 2, roundWidth / 2, viewWidth - roundWidth / 2, viewWidth - roundWidth / 2);
        paint.setColor(roundProgressColor);
        //画布上画进度条，这里的boolean值代表画出的进度有没有扇形的两个边
        //圆形进度为360°
        canvas.drawArc(rectF, 0, progress * 360 / max, false, paint);
        //canvas.drawArc(rectF, 0, progress * 360 / max, true, paint);
        //3. 绘制进度文本
        //文本内容
        String text = progress * 100 / max + "%";
        paint.setTextSize(textSize);
        paint.setStrokeWidth(0);
        //用矩形去框进度文本, 文本显示的宽高就是矩形的宽高
        Rect bounds = new Rect();//这里没有数据
        paint.setColor(textColor);
        paint.getTextBounds(text, 0, text.length(), bounds);
        float boundsWidth = bounds.width();//这里可以直接取数据
        float boundsHeight = bounds.height();
        //文本绘制原点是以左下角为基准的
        float textX = viewWidth / 2 - boundsWidth / 2;
        float textY = viewWidth / 2 + boundsHeight / 2;
        canvas.drawText(text, textX, textY, paint);
    }
}
