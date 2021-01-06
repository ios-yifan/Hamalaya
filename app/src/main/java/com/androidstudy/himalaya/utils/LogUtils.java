package com.androidstudy.himalaya.utils;

import android.util.Log;

public class LogUtils {

    public static String sTAG = "LogUtils";

    public static boolean sIsRelease = false;

    /*
    * 发布状态，可以设置为 release ，取消 log 输出
    * */
    public static void init(String baseTag, boolean isRelease){
        sTAG = baseTag;
        sIsRelease = isRelease;
    }

    public static void d(String TAG, String content){
        if (!sIsRelease){
            Log.d("[" + sTAG + "]" + TAG,content);
        }
    }

    public static void v(String TAG, String content){
        if (!sIsRelease){
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void i(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void w(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void e(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }
}
