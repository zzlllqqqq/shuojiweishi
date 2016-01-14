package com.atguigu.ms.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * 操作commonnum.db数据库表的dao类
 * @author 张晓飞
 *
 */
public class CommonNumberDao {

	/**
	 * 得到分组的列表
	 * 
	 * @return
	 */
	public static List<String> getGroupList(Context context) {
		List<String> list = new ArrayList<String>();

		// 创建连接对象
		String path = context.getFilesDir().getAbsolutePath() + "/commonnum.db";
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		// 查询
		Cursor cursor = database.rawQuery("select name from classlist", null);
		// 遍历封装
		while (cursor.moveToNext()) {
			String name = cursor.getString(0);
			list.add(name);
		}

		cursor.close();
		database.close();
		return list;
	}

	/**
	 * 得到所有的子列表
	 */
	public static List<List<String>> getChildList(Context context) {
		List<List<String>> list = new ArrayList<List<String>>();

		// 创建连接对象
		String path = context.getFilesDir().getAbsolutePath() + "/commonnum.db";
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		for (int i = 0; i < 8; i++) {
			String tableName = "table" + (i + 1);
			Cursor cursor = database.rawQuery("select name, number from "+tableName, null);
			List<String> childList = new ArrayList<String>();
			while(cursor.moveToNext()) {
				String name = cursor.getString(0);
				String number = cursor.getString(1);
				childList.add(name+"_"+number);
			}
			list.add(childList);
		}

		return list;
	}
}
