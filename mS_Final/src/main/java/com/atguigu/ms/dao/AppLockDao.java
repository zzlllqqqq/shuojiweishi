package com.atguigu.ms.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * 操作lock_app表的dao类
 * @author 张晓飞
 *
 */
public class AppLockDao {

	private DBHelper dbHelper;
	public AppLockDao(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	
	public void add(String packageName) {
		//1. 得到SqliteDatabase对象
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		//2. 执行insert
		ContentValues values = new ContentValues();
		values.put("package_name", packageName);
		long id = database.insert("lock_app", null, values);
		Log.i("TAG", "add() id="+id);
		//3. 关闭连接
		database.close();
	}
	
	public void delete(String packageName) {
		//1. 得到SqliteDatabase对象
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		//2. 执行delete
		int deleteCount = database.delete("lock_app", "package_name=?", new String[]{packageName});
		Log.i("TAG", "delete() deleteCount="+deleteCount);
		//3. 关闭连接
		database.close();
	}
	
	public List<String> getAllLocks(){
		List<String> list = new ArrayList<String>();
		//1. 得到SqliteDatabase对象
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		//2. 执行query
		Cursor cursor = database.rawQuery("select package_name from lock_app", null);
		//取出cursor中所有记录并保存到list中
		while(cursor.moveToNext()) {
			list.add(cursor.getString(0));
		}
		
		//3. 关闭结果集和连接
		cursor.close();
		database.close();
		
		return list;
	}
}
