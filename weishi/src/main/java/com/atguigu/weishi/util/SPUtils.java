package com.atguigu.weishi.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 2015/12/27.
 */
public final class SPUtils {

    public static final String SIM_NUMBER = "sim_number";
    public static final String PROTECT = "protect";
    private static SharedPreferences sp;
    private static SPUtils instanse;
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String CONFIGURE = "configure";
    public static final String SAFE_NUMBER = "safe_number";

    private SPUtils() {};

    public static SPUtils getInstanse(Context context) {
        if (instanse == null) {
            sp = context.getSharedPreferences("ms", Context.MODE_PRIVATE);
            instanse = new SPUtils();
        }
        return instanse;
    }

    public void save(String key, Object values) {
        if (values instanceof String) {
            sp.edit().putString(key, (String) values).commit();
        } else if (values instanceof Integer) {
            sp.edit().putInt(key, (Integer) values).commit();
        } else if (values instanceof Boolean) {
            sp.edit().putBoolean(key, (Boolean) values).commit();
        }
    }

    public <T> T getValue(String key, Object defValues) {
        T t = null;
        if (defValues == null || defValues instanceof String) {
            t = (T) sp.getString(key, (String) defValues);
        } else if (defValues instanceof Integer) {
            Integer value = sp.getInt(key, (Integer) defValues);
            t = (T) value;
        } else if (defValues instanceof Boolean) {
            Boolean value = sp.getBoolean(key, (Boolean) defValues);
            t = (T) value;
        }
        return t;
    }

    public void remove(String key) {
        sp.edit().remove(key).commit();
    }
}
