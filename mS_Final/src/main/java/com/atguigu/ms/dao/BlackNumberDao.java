package com.atguigu.ms.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.atguigu.ms.bean.BlackNumber;

/**
 * 操作black_number表的dao类
 * @author 张晓飞
 *
 */
public class BlackNumberDao {

	private DBHelper dbHelper;
	
	public BlackNumberDao(Context context) {
		dbHelper = new DBHelper(context);
	}
	/*
	 * 添加一个黑名单
	 */
	public void add(BlackNumber blackNumber) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		
		//insert
		ContentValues values = new ContentValues();
		values.put("number", blackNumber.getNumber());
		long id = database.insert("black_number", null, values );
		Log.i("TAG", "add id="+id);
		
		//保存产生的id
		blackNumber.setId((int) id);
		
		database.close();
		
	}
	
	/**
	 * 得到所有黑名单的列表
	 * @return
	 */
	public List<BlackNumber> getAll() {
		List<BlackNumber> list = new ArrayList<BlackNumber>();
		
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		
		//query
		String sql = "select * from black_number order by _id desc";
		Cursor cursor = database.rawQuery(sql , null);
		while(cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String number = cursor.getString(1);
			BlackNumber blackNumber = new BlackNumber(id, number);
			list.add(blackNumber);
		}
		
		cursor.close();
		database.close();
		
		return list;
	}
	
	/**
	 * 更新一个黑名单
	 * @param blackNumber
	 */
	public void update(BlackNumber blackNumber) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		
		//update
		ContentValues values = new ContentValues();
		values.put("number", blackNumber.getNumber());
		int updateCount = database.update("black_number", values, "_id="+blackNumber.getId(), null);
		Log.i("TAG", "update() updateCount="+updateCount);
		
		database.close();
	}
	
	/**
	 * 根据id删除一个黑名单
	 * @param id
	 */
	public void deleteById(int id) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		
		//delete
		int deleteCount = database.delete("black_number", "_id=?", new String[]{id+""});
		Log.i("TAG", "deleteById() deleteCount="+deleteCount);
		
		database.close();
	}
	
	/**
	 * 判断是否是黑名单号
	 * @param phoneNumber
	 * @return
	 */
	public boolean isBlack(String number) {
		
		boolean black = false;
		
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		
		//query
		String sql = "select * from black_number where number=?";
		Cursor cursor = database.rawQuery(sql , new String[]{number});
		
		black = cursor.getCount()>0; //是否有匹配的记录, 有则是黑名单
		
		cursor.close();
		database.close();
		
		return black;
	}
}
