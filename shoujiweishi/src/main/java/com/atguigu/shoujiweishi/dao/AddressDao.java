package com.atguigu.shoujiweishi.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by admin on 2015/12/29.
 */
public class AddressDao {
    private Context context;

    public AddressDao(Context context) {
        this.context = context;
    }

    /**
     * 查询得到对应的归属地
     *
     * @param number
     * @return
     */
    /*
    	手机号: 共11位数字, 第一个为: 1, 第二位为: 3—8, 前7位决定其归属地
    	匪警号码 : 3位
    	模拟器 : 4位
    	客服电话 : 5位
    	本地号码 : 6,7,8,9位
    	外地座机号: 大于或等于10位, 以0开头, 其第2,3位或2,3,4位决定其归属地
     */
    public String getAddress(String number) {
        String address = "位置归属地";

        String path = context.getFilesDir().getAbsolutePath() + "/address.db";

        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = null;

        //手机号: 共11位数字, 第一个为: 1, 第二位为: 3—8, 前7位决定其归属地
        try {
            String reg = "^1[345678]\\d{9}$";
            boolean isPhone = number.matches(reg);
            if(isPhone) {
                //取出Number中的前7位
                String prefix7 = number.substring(0, 7);
                String sql = "select location from data2 where id=(select outkey from data1 where id=?)";
                cursor = database.rawQuery(sql, new String[]{prefix7});
                if(cursor.moveToNext()) {
                    address = cursor.getString(0);
                }
            } else {
                switch (number.length()) {
                    case 3:
                        address = "匪警号码";
                        break;
                    case 4:
                        address = "模拟器";
                        break;
                    case 5:
                        address = "客服电话";
                        break;
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        address = "本地座机";
                        break;
                    default:
                        //外地座机号: 大于或等于10位, 以0开头, 其第2,3位或2,3,4位决定其归属地
                        if(number.length() >= 10 || number.startsWith("0")) {
                            String prefix23 = number.substring(1, 3);
                            String sql = "select location from data2 where area =?";
                            cursor = database.rawQuery(sql, new String[]{prefix23});
                            if(cursor.moveToNext()) {
                                address = cursor.getString(0);
                                address = address.substring(0, address.length() - 2) + "座机";
                            } else {
                                String prefix234 = number.substring(1, 4);
                                cursor = database.rawQuery(sql, new String[]{prefix234});
                                if(cursor.moveToNext()) {
                                    address = cursor.getString(0);
                                    address = address.substring(0, address.length() - 2) + "座机";
                                }
                            }
                        }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (database != null) {
                database.close();
            }
        }
        return address;
    }
}
