package com.tapura.podmorecasts;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    public static final String PODCAST_CACHE = "podcast_cache";

    private static MyApplication instance;

    public static MyApplication getApp() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Stetho.initializeWithDefaults(this);
        clearCache();
    }

    private void clearCache() {
        getSharedPreferences(PODCAST_CACHE, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
