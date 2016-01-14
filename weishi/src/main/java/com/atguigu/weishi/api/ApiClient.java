package com.atguigu.weishi.api;

import android.app.ProgressDialog;
import android.util.Xml;

import com.atguigu.weishi.bean.UpdateInfo;
import com.atguigu.weishi.util.Constants;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 2015/12/25.
 */
public final class ApiClient {
    public static UpdateInfo getUpdateInfo() throws Exception {
        UpdateInfo updateInfo = null;
//        updateInfo = getUpdateInfoFromJson();
//        return updateInfo;
        updateInfo = getUpdateInfoFromXml();
        return new UpdateInfo("1.2", "http://192.168.10.60:8080/ms_server/mobileSafe.apk", "优化网络请求,解决一些bug!");

    }

    private static UpdateInfo getUpdateInfoFromXml() throws Exception {
        UpdateInfo updateInfo = null;
        InputStream is = null;
        HttpURLConnection con = null;
        try {
            URL url = new URL(Constants.UPDATE_URL_XML);
            con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);
            con.getContent();
            int code = con.getResponseCode();
            if(code == 200) {
                is = con.getInputStream();
                updateInfo = new UpdateInfo();
                XmlPullParser xmlPullParser = Xml.newPullParser();
                xmlPullParser.setInput(is, "utf-8");
                int eventType = xmlPullParser.getEventType();
                while(eventType!=XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_TAG) {
                        String tagName = xmlPullParser.getName();
                        if("version".equals(tagName)) {
                            updateInfo.setVersion(xmlPullParser.nextText());
                        } else if("apkUrl".equals(tagName)){
                            String apkUrl = xmlPullParser.nextText();
                            updateInfo.setApkUrl(apkUrl);
                        } else if("desc".equals(tagName)){
                            String desc = xmlPullParser.nextText();
                            updateInfo.setDesc(desc);
                            break;
                        }
                    }
                    eventType = xmlPullParser.next();
                }
            } else {
                throw new RuntimeException("请求异常");
            }
        } finally {
            if (is!=null){
                is.close();
            }
            if (con!=null){
                con.disconnect();
            }
        }
        return updateInfo;
    }

    private static UpdateInfo getUpdateInfoFromJson() throws Exception {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        HttpURLConnection con = null;
        UpdateInfo updateInfo = null;
        try {
            URL url = new URL(Constants.UPDATE_URL_JSON);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.getContent();
            int code = con.getResponseCode();
            if (code == 200){
                is = con.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[2048];
                int len;
                while((len = is.read(buffer))!=-1){
                    baos.write(buffer,0,len);
                }
                String json = baos.toString();
                updateInfo = new Gson().fromJson(json, UpdateInfo.class);
            }else {
                throw new RuntimeException("请求异常");
            }
        } finally {
            if(baos!=null) {
                baos.close();
            }
            if(is!=null) {
                is.close();
            }
            if (con != null) {
                con.disconnect();
            }
        }
        return updateInfo;
    }

    public static void downloadAPK(ProgressDialog pd, File apkFile, String apkUrl) throws Exception {
        HttpURLConnection con = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try{
            URL url = new URL(apkUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);
            con.getContent();
            int code = con.getResponseCode();
            if (code == 200){
                pd.setMax(con.getContentLength());
                is = con.getInputStream();
                fos = new FileOutputStream(apkFile);
                byte[] buffer = new byte[2048];
                int len;
                while ((len = is.read(buffer))!=-1){
                    fos.write(buffer,0,len);
                    pd.incrementProgressBy(len);
                }
            } else {
                throw new RuntimeException("请求异常");
            }
        } finally {
            if(fos!=null) {
                fos.close();
            }
            if(is!=null) {
                is.close();
            }
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
