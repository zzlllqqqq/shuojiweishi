package com.atguigu.shoujiweishi.bean;

/**
 * Created by admin on 2015/12/25.
 */
public class UpdateInfo {

    /**
     * version : 1.3
     * apkUrl : http://192.168.10.165:8080/MsServer/MobileSafe.apk
     * desc : 优化网络请求,解决一些bug!!
     */
    private String version;
    private String apkUrl;
    private String desc;

    public UpdateInfo() {
    }

    public UpdateInfo(String version, String desc, String apkUrl) {
        this.version = version;
        this.desc = desc;
        this.apkUrl = apkUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "version='" + version + '\'' +
                ", apkUrl='" + apkUrl + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
