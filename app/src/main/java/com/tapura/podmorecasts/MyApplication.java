package com.tapura.podmorecasts;


import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Stetho.initializeWithDefaults(this);
        instance = this;
    }

    public static MyApplication getApp() {
        return instance;
    }
}
