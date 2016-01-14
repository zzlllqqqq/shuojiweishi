package com.atguigu.weishi.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.atguigu.weishi.bean.BlackNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/1/7.
 */
public class BlackNumberDao {
    private DBHelper dbHelper;

    public BlackNumberDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public List<BlackNumber> getAll(){
        List<BlackNumber> list = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "select * from black_number order by _id desc";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String number = cursor.getString(1);
            BlackNumber blackNumber = new BlackNumber(id, number);
            list.add(blackNumber);
        }
        cursor.close();
        database.close();
        return list;
    }

    public int add(BlackNumber blackNumber){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("number", blackNumber.getNumber());
            long id = database.insert("black_number", null, values);
            return (int) id;

    }

    public void delete(int id){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        database.delete("black_number", "_id=?", new String[]{id + ""});
        database.close();
    }

    public void update(BlackNumber blackNumber){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", blackNumber.getNumber());
        // update black_number set numner='' where id=''
        database.update("black_number", values, "_id="+blackNumber.getId(), null);
        database.close();
    }

    public boolean isBlack(String number){
        boolean black = false;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "select * from black_number where number = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{number});
        black = cursor.getCount()>0;
        cursor.close();
        database.close();
        return black;
    }
}
