package com.atguigu.shoujiweishi.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2015/12/31.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int version = 2;

    public DBHelper(Context context) {
        super(context, "ms.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表并添加初始化数据
        db.execSQL("create table black_number (_id integer primary key autoincrement , number varchar)");
        db.execSQL("insert into black_number (number) values ('110')");
        db.execSQL("insert into black_number (number) values ('120')");
        db.execSQL("insert into black_number (number) values ('119')");
        //由于不确定是否是首次安装该应用那么必须将两个表的创建都放在这里，避免首次使用时没有低版本安装才能生成的black_number表
        db.execSQL("create table app_lock (_id integer primary key autoincrement, package_name varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("create table app_lock (_id integer primary key autoincrement, package_name varchar)");
    }
}
