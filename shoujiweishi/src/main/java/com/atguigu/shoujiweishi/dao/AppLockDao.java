package com.atguigu.shoujiweishi.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/1/5.
 * 操作app_lock表的dao类
 */
public class AppLockDao {
    private DBHelper dbHelper;

    public AppLockDao(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public List<String> getAll(){
        List<String> list = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query("app_lock", new String[]{"package_name"}, null, null, null, null, null);
        while(cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close();
        database.close();
        return list;
    }

    public void add(String packageName){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("package_name", packageName);
        database.insert("app_lock", null, values);
        database.close();
    }

    public void delet(String packageName){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        database.delete("app_lock", "package_name = ?", new String[]{packageName});
        database.close();
    }
}
