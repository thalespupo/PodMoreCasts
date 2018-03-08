package com.tapura.podmorecasts;


import android.util.Log;

import java.io.File;

public class Utils {
    public static final String EPISODES_PATH = File.separator + MyApplication.getApp().getPackageName() + File.separator;

    public static String extractNameFrom(String episodeLink) {
        String[] strings = episodeLink.split("/");
        Log.d("THALES", "extractNameFrom: String URL:" + episodeLink);
        Log.d("THALES", "extractNameFrom: String: " + strings[strings.length - 1]);
        return strings[strings.length - 1];
    }
}
