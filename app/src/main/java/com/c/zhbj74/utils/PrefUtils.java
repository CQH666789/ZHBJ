package com.c.zhbj74.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {
    public static boolean getBoolean(Context ctx, String key, boolean defvalue){
        SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getBoolean(key, defvalue);
    }

    public static void setBoolean(Context ctx, String key, boolean value){
        SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static String getString(Context ctx, String key, String defvalue){
        SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(key, defvalue);
    }

    public static void setString(Context ctx, String key, String value){
        SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static int getString(Context ctx, String key, int defvalue){
        SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getInt(key, defvalue);
    }

    public static void setString(Context ctx, String key, int value){
        SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }
}
