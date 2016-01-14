package com.atguigu.ms.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 操作antivirus.db中表数据的dao类
 * @author 张晓飞
 *
 */
public class VirusDao {

	/**
	 * 判断是否是病毒应用
	 * @param context
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(Context context, String md5) {
		
		boolean virus = false;
		//得到连接对象
		String path = context.getFilesDir().getAbsolutePath()+"/antivirus.db";
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		//执行查询
		Cursor cursor = database.rawQuery("select * from datable where md5=?", new String[]{md5});
		//取出数据
		virus = cursor.getCount()>0;
		//关闭
		cursor.close();
		database.close();
		
		return virus;
	}

}
