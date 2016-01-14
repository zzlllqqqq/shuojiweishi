package com.atguigu.shoujiweishi.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.atguigu.shoujiweishi.BlackNumberActiity;
import com.atguigu.shoujiweishi.R;
import com.atguigu.shoujiweishi.dao.AddressDao;
import com.atguigu.shoujiweishi.dao.BlackNumberDao;
import com.atguigu.shoujiweishi.util.SPUtils;

import java.lang.reflect.Method;


/**
 * Created by admin on 2015/12/29.
 * 归属地服务
 * 监听电话状态,当响铃时, 显示来电归属地
 * TelephonyManager:监听电话状态
 * WindowManager: 显示来电归属地
 */
public class AddressService extends Service implements View.OnTouchListener {

    private TelephonyManager tm;
    private WindowManager wm;
    private AddressDao addressDao;
    private BlackNumberDao blackNumberDao;
    private TextView addressView;
    private PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //判断来电时骚扰电话
                    long duration = System.currentTimeMillis() - startTime;
                    //时间小于1s
                    if(duration < 5000) {
                        boolean contact = isContact(incomingNumber);
                        boolean black = blackNumberDao.isBlack(incomingNumber);
                        //不是黑名单号并且不是已有联系人号码
                        if(!contact && !black) {
                            showNotification(incomingNumber);
                        }
                    }
                    //移除归属地
                    removeAddress();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    startTime = System.currentTimeMillis();
                    //如果来电是黑名单号挂断电话  同时将通话记录删除
                    if (blackNumberDao.isBlack(incomingNumber)) {
                        endCall();
                        deleCallLog(incomingNumber);
                    } else {
                        shpwAddress(incomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    removeAddress();
                    break;
            }
        }
    };

    /**
     * 显示通知提醒
     * @param number
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification(String number) {
        //得到NotificationManager对象
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, BlackNumberActiity.class);
        intent.putExtra("number", number);
        //创建通知对象并设置
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("发现响一声电话")
                .setContentText("点击添加"+number+"为黑名单号")
                //与之前不同的地方就是这里，在这里设置将要实现的意图
                .setContentIntent(pendingIntent)
                .build();
        //设置通知的消失方式
        notification.flags = Notification.FLAG_AUTO_CANCEL;//触摸或者操作后消失
        //notification.flags = Notification.FLAG_NO_CLEAR; // 不能手动取消
        //发布通知消息
        manager.notify(2, notification);
    }

    /**
     * 判断是否是已存在的联系人
     * @param number
     * @return
     */
    private boolean isContact(String number) {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{number}, null);
        boolean contact = cursor.moveToNext();
        cursor.close();
        return contact;
    }

    private long startTime;

    /**
     * 删除通话记录
     * @param number
     */
    private void deleCallLog(final String number) {
        final Uri uri = CallLog.Calls.CONTENT_URI;
         /*
          * 第二个参数: 表示是否监听子路径
          * 第三个参数: 内容观察者(监听器)
          */
        getContentResolver().registerContentObserver(uri, true, new ContentObserver(null) {
            @Override
            //当时据改变时监听
            public void onChange(boolean selfChange) {
                getContentResolver().delete(uri, "number=?", new String[]{number});
                //解注册观察者
                getContentResolver().unregisterContentObserver(this);
            }
        });
    }

    /**
     * 挂断电话AIDL + 反射
     */
    private void endCall() {
        //通过反射得到隐藏的方法
        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getservice", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private WindowManager.LayoutParams params;

    /**
     * 显示来电归属地
     * 属性地
     * View
     * 显示
     *
     * @param incomingNumber
     */
    private void shpwAddress(String incomingNumber) {
        //1 得到归属地
        String address = addressDao.getAddress(incomingNumber);
        //2 动态加载用来显示归属地的TextView
        addressView = (TextView) View.inflate(this, R.layout.address_view, null);
        addressView.setText(address);
        //根据保存的样式显示背景图片
        int styleIndex = SPUtils.getInstance(this).getValue(SPUtils.STYLE_INDEX, 0);
        int[] bgIds = {R.drawable.call_locate_white, R.drawable.call_locate_orange,
                R.drawable.call_locate_green, R.drawable.call_locate_blue, R.drawable.call_locate_gray};
        addressView.setBackgroundResource(bgIds[styleIndex]);
        //根据保存的left/top指定位置
        int left = SPUtils.getInstance(this).getValue(SPUtils.UPLEFT, -1);
        int top = SPUtils.getInstance(this).getValue(SPUtils.UPTOP, -1);
        if (left == -1) {
            params.gravity = Gravity.LEFT | Gravity.TOP;
            params.x = left;
            params.y = top;
        }
        //实现addressView的移动
        addressView.setOnTouchListener(this);
        //3 用wm显示
        wm.addView(addressView, params);
    }

    private void removeAddress() {
        if (addressView != null) {
            wm.removeView(addressView);
            addressView = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        addressDao = new AddressDao(this);
        //这里声明blackNumberDao
        blackNumberDao = new BlackNumberDao(this);
        params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; // 宽度自适应
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度自适应
        params.format = PixelFormat.TRANSLUCENT;// 设置成透明的(否则会有黑色背景)
        params.type = WindowManager.LayoutParams.TYPE_PHONE; // 使addressView能移动
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //使addressView不用获得焦点(否则不能接电话)
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    private int lastX, lastY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                lastY = eventY;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = eventX - lastX;
                int dy = eventY - lastY;
                params.x += dx;
                params.y += dy;
                wm.updateViewLayout(addressView, params);
                lastY = eventY;
                lastX = eventX;
                break;
        }
        return true;
    }
}
