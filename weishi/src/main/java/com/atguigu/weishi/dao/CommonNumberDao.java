package com.atguigu.weishi.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/1/11.
 */
public class CommonNumberDao {

    public static List<String> getGroupList(Context context) {
        List<String> list = new ArrayList<>();
        String path = context.getFilesDir().getAbsolutePath() + "/commonnum.db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("select name from classlist", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            list.add(name);
        }
        cursor.close();
        database.close();
        return list;
    }

    public static List<List<String>> getChildList(Context context) {
        List<List<String>> list = new ArrayList<>();
        String path = context.getFilesDir().getAbsolutePath() + "/commonnum.db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        for (int i = 1; i <= 8; i++){
            List<String> list1 = new ArrayList<>();
            Cursor cursor = database.rawQuery("select name, number from table" + i, null);
            while(cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                list1.add(name + "_" + number);
            }
            cursor.close();
            list.add(list1);
        }
        database.close();
        return list;
    }
}
