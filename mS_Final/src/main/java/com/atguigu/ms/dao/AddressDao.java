package com.atguigu.ms.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 操作归属地数据库表数据的dao
 * @author 张晓飞
 *
 */
public class AddressDao {

	/*
	 	手机号: 共11位数字, 第一个为: 1, 第二位为: 3—8, 前7位决定其归属地
		匪警号码 : 3位
		模拟器 : 4位
		客服电话 : 5位
		本地号码 : 6,7,8,9位
		外地座机号: 大于或等于10位, 以0开头, 其第2,3位或2,3,4位决定其归属地
	 */
	private Context context;
	public AddressDao(Context context) {
		this.context = context;
	}
	
	
	public String getAddress(String number) {
		String address = "未知地区";
		
		//得到数据库连接对象
		// /data/data/packageName/files/address.db
		String path = context.getFilesDir().getAbsolutePath()+"/address.db";
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		
		//手机号 共11位数字, 第一个为: 1, 第二位为: 3—8
		//判断是否为手机号
		String reg = "^1[345678]\\d{9}$";
		if(number.matches(reg)) {//是手机号
			String sivePrefix = number.substring(0, 7);
			//查询表数据
			String sql = "select location from data2 where id=(select outkey from data1 where id=?)";
			Cursor cursor = database.rawQuery(sql, new String[]{sivePrefix});
			if(cursor.moveToNext()) {
				address = cursor.getString(0);
				cursor.close();
				database.close();
				return address;
			} 
		}
		
		/*
			 匪警号码 : 3位
			模拟器 : 4位
			客服电话 : 5位
			本地号码 : 6,7,8,9位
		 */
		switch (number.length()) {
		case 3: // 匪警号码
			address = "匪警号码";
			break;
		case 4://模拟器
			address = "模拟器";
			break;
		case 5://客服电话
			address = "客服电话";
			break;
		case 6:
		case 7:
		case 8:
		case 9: //本地座机号
			address = "本地座机";
			break;
		default:
			//外地座机号: 大于或等于10位, 以0开头, 其第2,3位或2,3,4位决定其归属地
				//大于或等于10位, 以0开头,
			if(number.length()>=10 && number.startsWith("0")) {
				//其第2,3位或2,3,4位决定其归属地
				//根据第2,3位查询
				String prefix23 = number.substring(1, 3);
				String sql = "select location from data2 where area =?";
				Cursor cursor = database.rawQuery(sql, new String[]{prefix23});
				if(cursor.moveToNext()) {
					String result = cursor.getString(0);//北京电信
					//北京
					address = result.substring(0, result.length()-2)+"座机";
				} else {
					//根据第2,3,4查询
					String prefix234 = number.substring(1, 4);
					cursor = database.rawQuery(sql, new String[]{prefix234});
					if(cursor.moveToNext()) {
						String result = cursor.getString(0);//湖北荆州电信
						//湖北荆州
						address = result.substring(0, result.length()-2)+"座机";
					}
				}
				cursor.close();
				database.close();
			}
			break;
		}
		
		return address;
	}

}
