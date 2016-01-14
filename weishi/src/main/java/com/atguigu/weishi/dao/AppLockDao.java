package com.atguigu.weishi.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/1/11.
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
        while (cursor.moveToNext()) {
            String string = cursor.getString(0);
            list.add(string);
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

    public void delete(String packageName){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        database.delete("app_lock", "package_name=?", new String[]{packageName});
        database.close();
    }
}
