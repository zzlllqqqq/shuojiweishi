package com.atguigu.weishi.util;

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
 * Created by admin on 2016/1/11.
 */
public final class SmsUtils {

    private static ProgressDialog pd;
    public static void reset(final Context context) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                pd = ProgressDialog.show(context, null, "正在还原...");
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                File file = new File(context.getFilesDir(), "sms.json");
                if(!file.exists()) {
                    MsUtils.showMsg(context, "没有备份短信");
                }
                try {
                    FileInputStream fis = new FileInputStream(file);
                    List<Map<String, String>> data = new Gson().fromJson(new InputStreamReader(fis, "utf-8"),
                                    new TypeToken<List<Map<String, String>>>(){}.getType());
                    ContentResolver resolver = context.getContentResolver();
                    Uri uri = Uri.parse("content://sms");
                    for (Map<String, String> map : data) {
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
                MsUtils.showMsg(context, "还原完成");
            }
        }.execute();
    }


    public static void backup(final Context context) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(context);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                ContentResolver resolver = context.getContentResolver();
                Uri uri = Uri.parse("content://sms");
                String[] comlumns = {"address", "date", "type", "body"};
                Cursor cursor = resolver.query(uri, comlumns, null, null, null);
                pd.setMax(cursor.getCount());
                List<Map<String, String>> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("address", cursor.getString(0));
                    map.put("date", cursor.getString(1));
                    map.put("type", cursor.getString(2));
                    map.put("body", cursor.getString(3));
                    list.add(map);
                    pd.incrementProgressBy(1);
                    SystemClock.sleep(50);
                }
                cursor.close();
                String json = new Gson().toJson(list);
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
                MsUtils.showMsg(context, "备份完成");
            }
        }.execute();
    }
}
