package com.tapura.podmorecasts;

import android.util.Log;


public class MyLog {

    private static final String TAG = "PodcastApp::";

    public static void v(Class c, String msg) {
        Log.v(getLogTag(c), msg);
    }

    public static void d(Class c, String msg) {
        Log.d(getLogTag(c), msg);
    }

    public static void i(Class c, String msg) {
        Log.i(getLogTag(c), msg);
    }

    public static void w(Class c, String msg) {
        Log.w(getLogTag(c), msg);
    }

    public static void e(Class c, String msg) {
        Log.e(getLogTag(c), msg);
    }

    public static void wtf(Class c, String msg) {
        Log.wtf(getLogTag(c), msg);
    }

    public static String getLogTag(Class c) {
        return TAG + c.getSimpleName();
    }
}
