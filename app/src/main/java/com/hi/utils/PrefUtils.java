package com.hi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.hi.app.App;
import com.hi.model.UserServer;

/**
 * SharePreference的封装
 *
 */

public class PrefUtils {

    public static final String PREF_NAME = "config";

    public static boolean getBoolean(Context ctx, String key,
                                     boolean defaultValue) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static void setBoolean(Context ctx, String key, boolean value) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static String getString(Context ctx, String key, String defaultValue) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void setString(Context ctx, String key, String value) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static void clearData(Context ctx, String key) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        sp.edit().remove(key).clear().commit();
    }



    public static void saveUserServerInfo(UserServer userServer) {
        String userInfo = JSON.toJSONString(userServer);
        PrefUtils.setString(App.getInstance().getApplicationContext(),"userInfo",userInfo);
    }

    public static UserServer getUserServerInfo() {
        String userInfo = PrefUtils.getString(App.getInstance().getApplicationContext(),"userInfo",null);
        UserServer user  =  JSON.parseObject(userInfo,UserServer.class);
        return user;
    }

    public static void ClearUserServerInfo() {
        PrefUtils.setString(App.getInstance().getApplicationContext(),"userInfo","");
    }
}
