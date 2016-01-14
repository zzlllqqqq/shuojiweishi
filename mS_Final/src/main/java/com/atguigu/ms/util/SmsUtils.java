package com.atguigu.ms.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Xml;
import android.widget.Toast;

/**
 * 短信备份与还原的工具类
 * @author 张晓飞
 *
 */
public final class SmsUtils {

	/**
	 * 还原短信
	 * @param context
	 */
	public static void restore(final Context context) {
		new AsyncTask<Void, Void, Void>() {

			//1. 在主线程, 显示提示视图
			@Override
			protected void onPreExecute() {
				pd = ProgressDialog.show(context, null, "正在还原中...");
			}
			
			//2. 在分线程, 处理长时间的操作(还原短信)
			@Override
			protected Void doInBackground(Void... params) {
				
				SystemClock.sleep(1000);
				
				try {
					//解析sms.xml文件
					XmlPullParser parser = Xml.newPullParser();
					FileInputStream fis = context.openFileInput("sms.xml");
					parser.setInput(fis, "utf-8");//读取文档的第一个数据
					int eventType = parser.getEventType();
					
					/*
				 	<?xml version="1.0" encoding="utf-8"?>
				 	<smses>
				 		<sms>
					 		<address>123</address>
					 		<date>123213213</date>
					 		<type>2</type>
					 		<body>abc</body>
					 	</sms>
					 	<sms>
					 		<address>123</address>
					 		<date>123213213</date>
					 		<type>2</type>
					 		<body>abc</body>
					 	</sms>
				 	</smses>
				 	
				 */
					
					//准备插入
					ContentResolver resolver = context.getContentResolver();
					Uri uri = Uri.parse("content://sms");
					ContentValues values = new ContentValues();
					
					while(eventType!=XmlPullParser.END_DOCUMENT) {
						
						if(eventType==XmlPullParser.START_TAG) {
							String tagName = parser.getName();
							if("address".equals(tagName)) {
								String address = parser.nextText();
								values.put("address", address);
							} else if("date".equals(tagName)) {
								String date = parser.nextText();
								values.put("date", date);
							} else if("type".equals(tagName)) {
								String type = parser.nextText();
								values.put("type", type);
							} else if("body".equals(tagName)) {
								String body = parser.nextText();
								values.put("body", body);
							}
						} else if(eventType==XmlPullParser.END_TAG) {
							String tagName = parser.getName();
							if("sms".equals(tagName)) {
								//数据准备好, 执行insert
								resolver.insert(uri, values);
								values.clear();//清理数据
							}
						}
						eventType = parser.next();
					}
					//将数据保存到短信表中
					
					//释放
					fis.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				pd.dismiss();
				Toast.makeText(context, "短信还原完成!", 0).show();
			}
		}.execute();
	}

	private static ProgressDialog pd;
	/**
	 * 备份短信
	 * @param aToolActivity
	 */
	public static void backup(final Context context) {
		new AsyncTask<Void, Void, Void>() {

			//1. 在主线程, 显示提示视图
			@Override
			protected void onPreExecute() {
				pd = new ProgressDialog(context);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
			}
			
			//2. 在分线程, 处理长时间的操作(备份短信)
			@Override
			protected Void doInBackground(Void... params) {
				try {
					//读取短信数据
					Uri uri = Uri.parse("content://sms");
					String[] projection = {"address", "date", "type", "body"};
					Cursor cursor = context.getContentResolver().query(uri, projection , null, null, null);
					
					//指定最大进度
					pd.setMax(cursor.getCount());
					
					
					//生成xml文件  /data/data/packageName/files/sms.xml
					XmlSerializer serializer =Xml.newSerializer();
					FileOutputStream fos = context.openFileOutput("sms.xml", Context.MODE_PRIVATE);
					serializer.setOutput(fos, "utf-8");
					
					//设计xml结构
					
					/*
					 	<?xml version="1.0" encoding="utf-8"?>
					 	<smses>
					 		<sms>
						 		<address>123</address>
						 		<date>123213213</date>
						 		<type>2</type>
						 		<body>abc</body>
						 	</sms>
						 	<sms>
						 		<address>123</address>
						 		<date>123213213</date>
						 		<type>2</type>
						 		<body>abc</body>
						 	</sms>
					 	</smses>
					 	
					 */
					//生成文档头
					serializer.startDocument("utf-8", true);
					//生成<smses>
					serializer.startTag(null, "smses");
					while(cursor.moveToNext()) {
						String address = cursor.getString(0);
						String date = cursor.getString(1);
						String type = cursor.getString(2);
						String body = cursor.getString(3);
						/*
						    <sms>
						 		<address>123</address>
						 		<date>123213213</date>
						 		<type>2</type>
						 		<body>abc</body>
						 	</sms>
						 */
						serializer.startTag(null, "sms")
								  .startTag(null, "address")
								  .text(address)
								  .endTag(null, "address")
								  .startTag(null, "date")
								  .text(date)
								  .endTag(null, "date")
								  .startTag(null, "type")
								  .text(type)
								  .endTag(null, "type")
								  .startTag(null, "body")
								  .text(body)
								  .endTag(null, "body")
								  .endTag(null, "sms");
						
						//更新进度
						pd.incrementProgressBy(1);
						//休息一会
						SystemClock.sleep(500);
					}
					
					//生成</smses>
					serializer.endTag(null, "smses");
					//标识文档写完了
					serializer.endDocument();
					
					//保存到xml文件中, 在保存过程中同步更新进度
					
					
					//释放
					fos.close();
					cursor.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				pd.dismiss();
				Toast.makeText(context, "短信备份完成", 0).show();
			}
		}.execute();
	}

}
