package com.tapura.podmorecasts;

import android.arch.lifecycle.Observer;
import android.util.Log;
import android.util.Pair;

import com.tapura.podmorecasts.model.Podcast;


public class MyLog {

    private static final String TAG = "PodcastApp::";

    public static void v(Class c, String msg) {
        Log.v(TAG + c.getSimpleName(), msg);
    }

    public static void d(Class c, String msg) {
        Log.d(TAG + c.getSimpleName(), msg);
    }

    public static void i(Class c, String msg) {
        Log.i(TAG + c.getSimpleName(), msg);
    }

    public static void w(Class c, String msg) {
        Log.w(TAG + c.getSimpleName(), msg);
    }

    public static void e(Class c, String msg) {
        Log.e(TAG + c.getSimpleName(), msg);
    }

    public static void wtf(Class c, String msg) {
        Log.wtf(TAG + c.getSimpleName(), msg);
    }
}
