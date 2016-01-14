package com.atguigu.shoujiweishi.api;

import android.app.ProgressDialog;
import android.util.Xml;

import com.atguigu.shoujiweishi.bean.UpdateInfo;
import com.atguigu.shoujiweishi.util.Constants;
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
 * 联网请求的工具类
 */
public final class ApiClient {

    /**
     * 联网请求得到apk相关信息(json/xml)对象
     * @return
     */
    public static UpdateInfo getUpdateInfo() throws Exception {
        UpdateInfo updateInfo = null;
        //用Xml及Pull解析
        //updateInfo = getUpdateInfoByXml();
        //return new UpdateInfo("1.2", "http://192.168.10.60:8080/ms_server/mobileSafe.apk", "优化网络请求,解决一些bug!");
        //用json解析
        updateInfo = getUpdateInfoByJson();
        return updateInfo;
    }

    private static UpdateInfo getUpdateInfoByJson() throws Exception {
        /**
         * 请求得到apk相关信息的json数据对象
         * @return
         */
        UpdateInfo updateInfo = null;
        HttpURLConnection con = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
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
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer))!=-1){
                    baos.write(buffer,0,len);
                }
                String reuslt = baos.toString();
                updateInfo = new Gson().fromJson(reuslt,UpdateInfo.class);
            } else {
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

    /**
     *
     * @return
     * @throws Exception

    <?xml version="1.0" encoding="UTF-8" ?>
    - <update>
    <version>1.3</version>
    <apkUrl>http://192.168.10.165:8080/MsServer/MobileSafe.apk</apkUrl>
    <desc>优化网络请求,解决一些bug!!!</desc>
    </update>
     */
    private static UpdateInfo getUpdateInfoByXml() throws Exception{
        UpdateInfo updateInfo = null;
        HttpURLConnection con = null;
        InputStream is = null;
        try {
            URL url = new URL(Constants.UPDATE_URL_XML);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.getContent();
            int code = con.getResponseCode();
            if (code == 200){
                is = con.getInputStream();
                updateInfo = new UpdateInfo();
                XmlPullParser pullParser = Xml.newPullParser();
                pullParser.setInput(is, "utf-8");
                int eventType = pullParser.getEventType();
                while (eventType!=XmlPullParser.END_DOCUMENT){
                    if (eventType == XmlPullParser.START_TAG){
                        String tagName = pullParser.getName();
                        if ("version".equals(tagName)){
                            String text = pullParser.nextText();
                            updateInfo.setVersion(text);
                        } else if("apkUrl".equals(tagName)){
                            String apkUrl = pullParser.nextText();
                            updateInfo.setApkUrl(apkUrl);
                        } else if("desc".equals(tagName)){
                            String desc = pullParser.nextText();
                            updateInfo.setDesc(desc);
                            break;
                        }
                    }
                    eventType = pullParser.next();
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

    /**
     * 下载apk并显示下载进度
     * @param pd
     * @param apkFile
     * @param apkUrl
     */
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
                byte[] buffer = new byte[1024];
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
