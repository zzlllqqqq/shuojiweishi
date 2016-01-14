package com.atguigu.ms.provider;

import com.atguigu.ms.dao.AppLockDao;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * 操作lock_app表的provider
 * @author 张晓飞
 *
 */
public class AppLockProvider extends ContentProvider {

	private AppLockDao dao;
	// content://com.atguigu.ms.provider.applockprovider/lock_app/
	// content://com.atguigu.ms.provider.applockprovider/lock_app/3
	private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		matcher.addURI("com.atguigu.ms.provider.applockprovider", "/lock_app", 1);
		matcher.addURI("com.atguigu.ms.provider.applockprovider", "/lock_app/#", 2);
	}
	
	
	@Override
	public boolean onCreate() {
		dao = new AppLockDao(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	//content://com.atguigu.ms.provider.applockprovider/lock_app/
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.i("TAG", "AppLockProvider insert() ");
		
		//添加一个锁定包名
		dao.add(values.getAsString("package_name"));
		//通知所有监视在uri上的观察者
		getContext().getContentResolver().notifyChange(uri, null);
		return uri;
	}

	//content://com.atguigu.ms.provider.applockprovider/lock_app/
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.i("TAG", "AppLockProvider delete() ");
		
		//删除一个锁定包名
		dao.delete(selectionArgs[0]);
		//通知所有监视在uri上的观察者
		getContext().getContentResolver().notifyChange(uri, null);
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
