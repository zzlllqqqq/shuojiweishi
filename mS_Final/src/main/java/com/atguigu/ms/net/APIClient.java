package com.atguigu.ms.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Context;

import com.atguigu.ms.bean.UpdateInfo;
import com.atguigu.ms.util.Constants;
import com.google.gson.Gson;

/**
 * 请求服务器的工具类
 * @author 张晓飞
 *
 */
public final class APIClient {

	/**
	 * 得到最新版本的信息对象
	 * @return
	 * @throws Exception 
	 */
	public static UpdateInfo getUpdateInfo() throws Exception {
		
		String updateUrl =  Constants.URL_UPDATE_XML;
		updateUrl = Constants.URL_UPDATE_JSON;
		
		//1. 请求服务器得到对应的流数据   inputStream
		InputStream is = requestServer(updateUrl);
		
		//2. 解析xml流,并封装成udpateInfo对象
		//UpdateInfo info = parseXml(is);
		//2. 解析json流,并封装成udpateInfo对象
		UpdateInfo info = parseJson(is);
		
		return info;
	}

	/**
	 * 解析json数据流返回对应的数据信息对象(使用原生的API)
	 * @param is
	 * @return
	 * @throws Exception 
	 */
	private static UpdateInfo parseJson(InputStream is) throws Exception {
		String jsonString = readToString(is);
		JSONObject jsonObject = new JSONObject(jsonString);
		String version = jsonObject.getString("version");
		String apkUrl = jsonObject.getString("apkUrl");
		String desc = jsonObject.getString("desc");
		
		return new UpdateInfo(version, apkUrl, desc);
	}
	
	private static String readToString(InputStream is) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while((len=is.read(buffer))>0) {
			baos.write(buffer, 0, len);
		}
		baos.close();
		is.close();
		return baos.toString();
	}

	/**
	 * 解析json数据流返回对应的数据信息对象(使用gson框架)
	 * @param is
	 * @return
	 * @throws Exception 
	 */
	private static UpdateInfo parseJson2(InputStream is) throws Exception {
		return new Gson().fromJson(new InputStreamReader(is, "utf-8"), UpdateInfo.class);
	}

	/**
	 * 解析xml数据流返回对应的数据信息对象
	 * @param is
	 * @return
	 * @throws Exception 
	 */
	private static UpdateInfo parseXml(InputStream is) throws Exception {
		UpdateInfo info = new UpdateInfo();
		//得到解析器
		XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
		//解析
		parser.setInput(is, "utf-8");
		//得到第一个解析数据的事件类型
		int eventType = parser.getEventType();
		
		while(eventType!=XmlPullParser.END_DOCUMENT) {
			
			if(eventType==XmlPullParser.START_TAG) {//读取了开始标签
				//得到标签名
				String tagName = parser.getName();
				if("version".equals(tagName)) {
					info.setVersion(parser.nextText());
				} else if("apkUrl".equals(tagName)) {
					info.setApkUrl(parser.nextText());
				} else if("desc".equals(tagName)) {
					info.setDesc(parser.nextText());
				}
			}
			//解析下一个数据
			eventType = parser.next();
		}
		
		
		
		return info;
	}

	/**
	 * 请求服务器得到数据流
	 * @param updateUrl
	 * @return
	 * @throws Exception
	 */
	private static InputStream requestServer(String path) throws Exception {
		
		//模拟网速慢 
		Thread.sleep(1000);
		
		InputStream is = null;
		// 1). 得到HttpUrlConnection对象
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 2). 设置
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(5000);
		// 3). 连接
		conn.connect();
		// 4). 请求并获取服务器端返回的数据流
		int responseCode = conn.getResponseCode();
		if(responseCode==200) {
			is = conn.getInputStream();
		}
		
		return is;
	}

	/**
	 * 下载apk文件, 并同步显示进度
	 * @param applicationContext
	 * @param pd
	 * @param apkFile
	 * @param apkUrl
	 * @throws Exception 
	 */
	public static void downloadAPK(Context applicationContext,
			ProgressDialog pd, File apkFile, String apkUrl) throws Exception {
		
		InputStream is = null;
		// 1). 得到HttpUrlConnection对象
		URL url = new URL(apkUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 2). 设置
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		// 3). 连接
		conn.connect();
		// 4). 请求并获取服务器端返回的数据流
		int responseCode = conn.getResponseCode();
		if(responseCode==200) {
			//设置最大进度
			pd.setMax(conn.getContentLength());
			is = conn.getInputStream();
			//得到apk文件的输出流
			FileOutputStream fos = new FileOutputStream(apkFile);
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len=is.read(buffer))>0) {
				//写数据
				fos.write(buffer, 0, len);
				//更新进度
				pd.incrementProgressBy(len);
				//模拟网速慢 
				Thread.sleep(20);
			}
			fos.close();
			is.close();
		}
		//5). 关闭流, 连接
		conn.disconnect();
	}

}
