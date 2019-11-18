package com.utravel.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    public static final String APP = "IGo";

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(APP, context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(APP, context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }
    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(APP, context.MODE_PRIVATE);
        return sp.getString(key, "");
    }
    public static void putString(Context context,String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(APP, context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }
    public static int getInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(APP, context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }
    public static void putInt(Context context,String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(APP, context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }
    public static void remove(Context context,String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP, context.MODE_PRIVATE).edit();
        editor.remove(key);
        editor.commit();
    }

    public static void clearlogin(Context context) {
        remove(context, "id");
        remove(context, "mobile");
        remove(context, "token");
        remove(context, "Token");
    }

    public static void removeKey(Context context, String key) {
        remove(context, key);
    }
}
