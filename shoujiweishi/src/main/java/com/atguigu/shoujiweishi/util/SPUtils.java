package com.atguigu.shoujiweishi.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 2015/12/27.
 * sp存储的工具类
 * <p/>
 * 保存:
 * void save(String key, Object value)
 * 读取:
 * <T> T </T>getVlaue(String key, Object defValue)
 * 移除
 * void remove(String key)
 */

public final class SPUtils {
    public static final String SIM_NUMBER = "sim_number";
    public static final String PROTECT = "protect";
    public static final String UPLEFT = "upletf";
    public static final String UPTOP = "uptop";
    public static final String STYLE_INDEX = "style_index";
    private static SharedPreferences sp;
    private static SPUtils instance;
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String CONFIGURE = "configure";
    public static final String SAFE_NUMBER = "safe_number";
    public static final String SHORT_CUT = "short_cut";

    private SPUtils() {};

    public static SPUtils getInstance(Context context) {
        if (instance == null) {
            sp = context.getSharedPreferences("ms", Context.MODE_PRIVATE);
            instance = new SPUtils();
        }
        return instance;
    }

    public void save(String key, Object value) {
        if (value instanceof String) {
            sp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Integer) {
            sp.edit().putInt(key, (Integer) value).commit();
        } else if (value instanceof Boolean) {
            sp.edit().putBoolean(key, (Boolean) value).commit();
        }
    }

    public <T> T getValue(String key, Object defValue) {
        T t = null;
        if (defValue == null || defValue instanceof String) {
            t = (T) sp.getString(key, (String) defValue);
        } else if (defValue instanceof Boolean) {
            Boolean value = sp.getBoolean(key, (Boolean) defValue);
            t = (T) value;
        } else if (defValue instanceof Integer) {
            Integer value = sp.getInt(key, (Integer) defValue);
            t = (T) value;
        }
        return t;
    }

    public void remove(String key) {
        sp.edit().remove(key).commit();
    }

}
