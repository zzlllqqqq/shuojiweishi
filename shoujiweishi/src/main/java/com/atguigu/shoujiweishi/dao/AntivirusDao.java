package com.atguigu.shoujiweishi.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by admin on 2016/1/9.
 */
public class AntivirusDao {

    public static boolean isVirus(Context context, String md5){
        String path = context.getFilesDir().getAbsolutePath() + "/antivirus.db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("SELECT * FROM datable WHERE md5=?", new String[]{md5});
        boolean isVirus = cursor.moveToNext();
        cursor.close();
        database.close();
        return isVirus;
    }

}
