package com.c.zhbj74.utils;

import android.content.Context;

/*
* 网络缓存的工具类
* */
public class CacheUtils {
    /*
    * 以url为key，以json为value,保存在本地
    * */
    public static void setCache(String url, String json, Context ctx){
        //也可以用文件缓存：以MD5(url)为文件名，以json为文件内容
        PrefUtils.setString(ctx,url,json);
    }
    /*
    * 获取缓存
    * */
    public static String getCache(String url,Context ctx){
        return PrefUtils.getString(ctx,url,null);
    }
}
