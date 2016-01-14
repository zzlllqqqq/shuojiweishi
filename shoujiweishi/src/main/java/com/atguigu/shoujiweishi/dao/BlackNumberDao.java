package com.atguigu.shoujiweishi.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.atguigu.shoujiweishi.bean.BlackNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2015/12/31.
 * 操作black_number的dao类
 */
public class BlackNumberDao {
    private DBHelper dbHelper;

    public BlackNumberDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * 得到表中的所有数据
     * @return
     */
    public List<BlackNumber> getAll(){
        List<BlackNumber> list = new ArrayList<>();
        //得到链接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "select * from black_number order by _id desc";
        //执行sql语句进行查询得到cursor对象
        Cursor cursor = database.rawQuery(sql, null);
        //遍历cursor取出所有的数据保存至list
        while(cursor.moveToNext()) {
            //第一个位置是id
            int id = cursor.getInt(0);
            //第二个位置是number
            String number = cursor.getString(1);
            BlackNumber blackNumber = new BlackNumber(id, number);
            list.add(blackNumber);
        }
        //关闭
        cursor.close();
        database.close();
        return list;
    }

    public int add(BlackNumber blackNumber){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", blackNumber.getNumber());
        //insert into black_number number values ('')
        long id = database.insert("black_number", null, values);
        Log.i("TAG", "BlackNumberDao add() " + id);
        database.close();
        //在这里强转之后就不用在调用时候进行强转
        return (int) id;
    }

    public void deletById(int id){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //delete from black_number where _id = ?
        int deleteCount = database.delete("black_number", "_id = ?", new String[]{id + ""});
        Log.i("TAG", "BlackNumberDao deleteById() "+deleteCount);
        database.close();
    }

    public void update(BlackNumber blackNumber){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", blackNumber.getNumber());
        //update black_number set number = '' where _id = ?
        int updateCount = database.update("black_number", values, "_id=" + blackNumber.getId(), null);
        Log.i("TAG", "BlackNumberDao update() " + updateCount);
        database.close();
    }

    /**
     * 判断这个号码是不是黑名单号码
     * @param number
     * @return
     */
    public boolean isBlack(String number) {
        boolean black = false;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "select * from black_number where number = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{number});
        black = cursor.getCount() > 0;
        //black = cursor.moveToNext();
        cursor.close();
        database.close();
        return black;
    }
}
