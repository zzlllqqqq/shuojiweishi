package com.atguigu.ms.test;

import java.util.List;

import com.atguigu.ms.bean.BlackNumber;
import com.atguigu.ms.dao.BlackNumberDao;

import android.test.AndroidTestCase;
import android.util.Log;

/**
 * BlackNumber的单元测试类
 * @author 张晓飞
 *
 */
public class BlackNumberDaoTest extends AndroidTestCase{

	public void testAdd() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.add(new BlackNumber(-1, "120"));
	}
	
	public void testGetAll() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		List<BlackNumber> list = dao.getAll();
		Log.i("TAG", list.toString());
	}
	
	public void testUpdate() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update(new BlackNumber(2, "119"));
	}
	
	public void testDeleteById() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.deleteById(2);
	}
	
	
}
