package com.atguigu.shoujiweishi.util;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/1/8.
 * 短信备份和还原的工具类
 */
public class SmsUtils {

    private static ProgressDialog pd;

    /**
     * 异步还原短信
     * @param context
     */
    public static void reset(final Context context) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                pd = ProgressDialog.show(context, null, "正在还原中....");
            }

            @Override
            protected Void doInBackground(Void... params) {
                //1). 读取json数据并解析为List<Map<String, String>
                File file = new File(context.getFilesDir(), "sms.json");
                if(!file.exists()) {
                    return null;
                }
                try {
                    FileInputStream fis = new FileInputStream(file);
                    //解析json文件流, 并转换为一个集合
                    List<Map<String, String>> list = new Gson().fromJson(new InputStreamReader(fis, "utf-8"),
                            new TypeToken<List<Map<String, String>>>(){}.getType());
                    //2). 保存sms表中
                    ContentResolver resolver = context.getContentResolver();
                    Uri uri = Uri.parse("content://sms");
                    for (Map<String, String> map : list) {
                        ContentValues values = new ContentValues();
                        values.put("address", map.get("address"));
                        values.put("date", map.get("date"));
                        values.put("type", map.get("type"));
                        values.put("body", map.get("body"));
                        resolver.insert(uri, values);
                    }
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                pd.dismiss();
                MSUtils.showMsg(context, "还原完成");
            }
        }.execute();
    }


    /**
     * 异步备份短信
     * @param context
     */
    public static void backup(final Context context) {
        new AsyncTask<Void, Void, Void>(){

            //1. 主线程, 显示提示dialog
            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(context);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.show();
            }

            //2. 分线程备份
            @Override
            protected Void doInBackground(Void... params) {
                //1). 查询sms表数据List<Map<String, String>
                ContentResolver resolver = context.getContentResolver();
                Uri uri = Uri.parse("content://sms");
                String[] comlumns = {"address", "date", "type", "body"};
                Cursor cursor = resolver.query(uri, comlumns, null, null, null);
                List<Map<String, String>> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("address", cursor.getString(0));
                    map.put("date", cursor.getString(1));
                    map.put("type", cursor.getString(2));
                    map.put("body", cursor.getString(3));
                    list.add(map);
                    pd.incrementProgressBy(1);
                    SystemClock.sleep(200);
                }
                cursor.close();
                //2). 转换为Json
                Gson gson = new Gson();
                String json = gson.toJson(list);
                //3). 保存到指定文件中: /data/data/packageName/files/sms.json
                try {
                    FileOutputStream fos = context.openFileOutput("sms.json", Context.MODE_PRIVATE);
                    fos.write(json.getBytes("utf-8"));
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                pd.dismiss();
                MSUtils.showMsg(context, "备份完成");
            }
        }.execute();
    }
}
