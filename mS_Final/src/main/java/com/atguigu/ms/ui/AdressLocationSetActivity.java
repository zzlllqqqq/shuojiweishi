package com.atguigu.ms.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.atguigu.ms.R;
import com.atguigu.ms.util.SpUtils;

/**
 * 设置归属地位置的界面
 * @author 张晓飞
 *
 */
public class AdressLocationSetActivity extends Activity implements OnTouchListener {

	private LinearLayout ll_location_set;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adress_location_set);
		
		ll_location_set = (LinearLayout) findViewById(R.id.ll_location_set);
		
		//设置touch监听
		ll_location_set.setOnTouchListener(this);
		
		//读取保存的坐标
		int left = SpUtils.getInstance(this).get(SpUtils.LEFT, 0); //100
		int top = SpUtils.getInstance(this).get(SpUtils.TOP, 0); //100

		//ll_location_set.layout(left, top, r, b);
		//动态指定视图位置
		LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_location_set.getLayoutParams();
		layoutParams.leftMargin = left;
		layoutParams.topMargin = top;
		ll_location_set.setLayoutParams(layoutParams);
		
		//指定背景图片
		int index = SpUtils.getInstance(this).getInt(SpUtils.STYLE_INDEX, 0);
		int[] icons = {R.drawable.call_locate_white, R.drawable.call_locate_orange,R.drawable.call_locate_green,
				R.drawable.call_locate_blue, R.drawable.call_locate_gray};
		ll_location_set.setBackgroundResource(icons[index]);
	}

	private int lastX;
	private int lastY;
	private int parentRight;
	private int parentBottom;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			//得到父视图的rigth/bottom
			if(parentRight==0) {
				ViewGroup parentView = (ViewGroup) ll_location_set.getParent();
				parentRight = parentView.getRight();
				parentBottom = parentView.getBottom();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int movex = (int) event.getRawX();
			int moveY = (int) event.getRawY();
			int distanceX = movex-lastX;
			int distanceY = moveY-lastY;
			//得到新的坐标
			int left = ll_location_set.getLeft()+distanceX;
			int top = ll_location_set.getTop()+distanceY;
			int right = ll_location_set.getRight()+distanceX;
			int bottom = ll_location_set.getBottom()+distanceY;
			//限制拖动的范围
			//限制left
			if(left<0) {
				right = right-left;
				left = 0;
			}
			//限制top
			if(top<0) {
				bottom = bottom-top;
				top = 0;
			}
			//限制right
			if(right>parentRight) {
				left = left+parentRight-right;
				right = parentRight;
			}
			//限制bottom
			if(bottom>parentBottom) {
				top = top+parentBottom-bottom;
				bottom = parentBottom;
			}
			
			//重新布局定位
			ll_location_set.layout(left, top, right, bottom);
			//更新last
			lastX = movex;
			lastY = moveY;
			break;
		case MotionEvent.ACTION_UP:
			//得到视图的左顶点的坐标
			int upLeft = ll_location_set.getLeft();
			int upTop = ll_location_set.getTop();
			//保存
			SpUtils.getInstance(this).save(SpUtils.LEFT, upLeft);
			SpUtils.getInstance(this).save(SpUtils.TOP, upTop);
			break;

		default:
			break;
		}
		
		
		return true;
	}
}

/*
//down
int lastX = event.getRawX();
int lastY = event.getRawY();
//move
int movex = event.getRawx();
int moveY = event.getRawY();
int distanceX = moveX-lastX()
int left = view.getLeft()+distanceX;
int top = view.getTop()+distanceY;
int right = view.getRight()+distanceX;
int bottom = view.getBottom()+distanceY;
view.layout(left, top, right, bottom)
lastX = moveX;
lastY = moveY;
*/