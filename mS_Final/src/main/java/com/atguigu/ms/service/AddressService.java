package com.atguigu.ms.service;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.atguigu.ms.R;
import com.atguigu.ms.dao.AddressDao;
import com.atguigu.ms.dao.BlackNumberDao;
import com.atguigu.ms.ui.BlackManagerActivity;
import com.atguigu.ms.util.SpUtils;

/**
 * 归属地操作服务
 * @author 张晓飞
 * 功能:
 *  1. 当电话响铃时, 在指定的位置显示来电归属地
 *  2. 当接通或挂断电话时, 让来电归属地消失
 *  
 *  监听电话状态
 *  当为响铃时, 根据电话号得到归属地, 在窗口中显示出现
 */
@SuppressLint("NewApi")
public class AddressService extends Service {

	private TelephonyManager tm;
	private WindowManager wm;
	private AddressDao addressDao;
	private WindowManager.LayoutParams params;
	private BlackNumberDao blackNumberDao;
	
	private long startTime;
	
	//电话监听器
	private PhoneStateListener listener = new PhoneStateListener(){
		public void onCallStateChanged(int state, String incomingNumber) {//电话状态改变
			/**
		     * @see TelephonyManager#CALL_STATE_IDLE  //没有来电或挂断电话
		     * @see TelephonyManager#CALL_STATE_RINGING //响铃没有接
		     * @see TelephonyManager#CALL_STATE_OFFHOOK //接通电话
		     */
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: //没有来电和挂断电话
				//判断是否是骚扰电话, 如果发通知
				long time = System.currentTimeMillis();
				//时间要在1s以内
				if(time-startTime<1000  && time-startTime>0)  {
					Log.e("TAG", "++++++++++++");
					//不是黑名单
					boolean black = blackNumberDao.isBlack(incomingNumber);
					Log.e("TAG", "++++++++++++2 "+black);
					//不是联系人号
					boolean contact = isContact(incomingNumber);
					Log.e("TAG", "++++++++++++3 "+contact);
					if(!black && !contact) {
						showNotification(incomingNumber);
					}
				}
				
				
				removeAddressView();
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//开始统计时间
				startTime = System.currentTimeMillis();
				
				//判断是否是黑名单号, 如果是直接挂断
				if(blackNumberDao.isBlack(incomingNumber)) {
					//挂断电话
					endCall();
					//删除通话记录(通话记录数据没有保存到表中)
					deleteCallLog(incomingNumber);
					
				} else {
					//如果不是, 显示来电归属地
					showAddressView(incomingNumber);
				}
				
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				removeAddressView();
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("TAG", "AddressService onCreate()");
		
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		addressDao = new AddressDao(this);
		blackNumberDao = new BlackNumberDao(this);
		
		//设置电话监听
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//创建布局参数对象
		params = new WindowManager.LayoutParams();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT; // 宽度自适应
		params.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度自适应
		params.format = PixelFormat.TRANSLUCENT;// 设置成半透明的
		params.type = WindowManager.LayoutParams.TYPE_PHONE; // 使addressView能移动
		params.flags = WindowManager.LayoutParams. FLAG_NOT_FOCUSABLE; //使addressView不用获得焦点
		
	}
	
	/**
	 * 显示提示骚扰电话通知
	 */
	protected void showNotification(String number) {
		//得到通知管理器
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		Intent intent = new Intent(this, BlackManagerActivity.class);
		//携带号码
		intent.putExtra("number", number);
		
		//将要使用的intent
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
		
		//创建通知对象
		Notification notification = new Notification.Builder(this)
					.setContentTitle("发现一个疑似骚扰电话")//标题
					.setContentText("点击"+number+"添加为黑名单")
					.setSmallIcon(R.drawable.logo)
					.setContentIntent(pendingIntent)
					.build();
		//FLAG_NO_CLEAR 设置不能人为的移除
		notification.flags = Notification.FLAG_AUTO_CANCEL;//点击就会自动移除
		
		//发通知
		manager.notify(0, notification);
		
		//manager.cancel(1); //动态取消通知
		
	}

	/**
	 * 判断是否是联系人号
	 * @param incomingNumber
	 * @return
	 */
	protected boolean isContact(String number) {
		boolean contact = false;
		Cursor cursor = getContentResolver().query(Phone.CONTENT_URI, null, Phone.NUMBER+"=?", new String[]{number}, null);
		contact = cursor.getCount()>0;
		cursor.close();
		return contact;
	}

	/**
	 * 删除通话记录
	 * @param incomingNumber
	 */
	private void deleteCallLog(final String number) {
		
		getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new ContentObserver(null) {
			@Override
			public void onChange(boolean selfChange) {//通话记录的表数据有变化
				//根据number删除对应的记录
				getContentResolver().delete(CallLog.Calls.CONTENT_URI, "number=?", new String[]{number});
				//解注册内容观察者
				getContentResolver().unregisterContentObserver(this);
			}
		});
	}

	/**
	 * 挂断电话
	 */
	private void endCall() {
		try {
			// 通过反射拿到android.os.ServiceManager里面的getService这个方法的对象
			Class clazz = Class.forName("android.os.ServiceManager");
			Method method = clazz.getMethod("getService", String.class);
			// 通过反射调用这个getService方法，然后拿到IBinder对象，然后就可以进行aidl啦
			IBinder iBinder = (IBinder) method.invoke(null,
					new Object[] { Context.TELEPHONY_SERVICE });
			ITelephony telephony = ITelephony.Stub.asInterface(iBinder);
			telephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 移除来电地区View
	 */
	protected void removeAddressView() {
		if(addressView!=null) {
			wm.removeView(addressView);
			addressView = null;
		}
	}

	private View addressView;
	private int lastX;
	private int lastY;
	/**
	 * 显示来电地区View
	 */
	private void showAddressView(String number) {
		
		//加载布局addressView
		addressView = View.inflate(this, R.layout.address_view, null);
		
		//设置touch监听
		addressView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int movex = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					int distanceX = movex-lastX;
					int distanceY = moveY-lastY;
					//更新坐标显示
					params.x += distanceX;
					params.y += distanceY;
					wm.updateViewLayout(addressView, params);
					
					lastX = movex;
					lastY = moveY;
					break;
				default:
					break;
				}
				return true;
			}
		});
		
		//设置背景图片
		int index = SpUtils.getInstance(this).getInt(SpUtils.STYLE_INDEX, 0);
		int[] icons = {R.drawable.call_locate_white, R.drawable.call_locate_orange,R.drawable.call_locate_green,
				R.drawable.call_locate_blue, R.drawable.call_locate_gray};
		addressView.setBackgroundResource(icons[index]);
		
		TextView addressTV = (TextView) addressView.findViewById(R.id.tv_adress);
		String address = addressDao.getAddress(number);
		//设置来电归属地
		addressTV.setText(address);
		
		//根据指定位置来显示
		int left = SpUtils.getInstance(this).getInt(SpUtils.LEFT, -1);
		int top = SpUtils.getInstance(this).getInt(SpUtils.TOP, -1);
		if(left!=-1) {
			params.gravity = Gravity.LEFT|Gravity.TOP;
			params.x = left;
			params.y = top;
		}
		
		wm.addView(addressView, params);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("TAG", "AddressService onDestroy()");
		//停止电话监听 
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
