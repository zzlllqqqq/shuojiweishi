package com.atguigu.shoujiweishi;

import android.test.AndroidTestCase;
import android.util.Log;

import com.atguigu.shoujiweishi.bean.BlackNumber;
import com.atguigu.shoujiweishi.dao.BlackNumberDao;

import java.util.List;

/**
 * Created by xfzhang on 2015/12/31.
 * BlackNumberDao的单元测试类
 */
public class BlackNumberDaoTest extends AndroidTestCase {

    public void testGetAll() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(getContext());
        List<BlackNumber> list = blackNumberDao.getAll();
        Log.i("TAG", list.toString());
    }
    public void testAdd() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(getContext());
        blackNumberDao.add(new BlackNumber(-1, "5555"));
    }
    public void testUpdate() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(getContext());
        blackNumberDao.update(new BlackNumber(4, "6666"));
    }
    public void testDeleteById() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(getContext());
        blackNumberDao.deletById(5);
    }


}
