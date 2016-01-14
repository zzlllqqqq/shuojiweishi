package com.atguigu.ms.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库操作的工具类
 * @author 张晓飞
 *
 */
public final class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, "ms.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table black_number(_id integer primary key autoincrement, number varchar)");
		db.execSQL("create table lock_app(_id integer primary key autoincrement, package_name varchar)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
