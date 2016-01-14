package com.atguigu.weishi.bean;

/**
 * Created by admin on 2015/12/25.
 */
public class UpdateInfo {

    private String version;
    private String apkUrl;
    private String desc;

    public UpdateInfo() {
    }

    public UpdateInfo(String version, String apkUrl, String desc) {
        this.version = version;
        this.apkUrl = apkUrl;
        this.desc = desc;
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
