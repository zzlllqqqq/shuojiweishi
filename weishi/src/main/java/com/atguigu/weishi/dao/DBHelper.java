package com.atguigu.weishi.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2016/1/7.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final int version = 2;

    public DBHelper(Context context) {
        super(context, "ms.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table black_number (_id integer primary key autoincrement, number varchar)");
        db.execSQL("insert into black_number (number) values ('111')");
        db.execSQL("insert into black_number (number) values ('222')");
        db.execSQL("insert into black_number (number) values ('333')");

        db.execSQL("create table app_lock (_id integer primary key autoincrement, package_name varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("create table app_lock (_id integer primary key autoincrement, package_name varchar)");
    }
}
