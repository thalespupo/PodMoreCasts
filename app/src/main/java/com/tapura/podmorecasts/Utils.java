package com.tapura.podmorecasts;


import java.io.File;

public class Utils {
    public static final String EPISODES_PATH = File.separator + MyApplication.getApp().getPackageName() + File.separator;

    public static String extractNameFrom(String episodeLink) {
        String[] strings = episodeLink.split("/");
        MyLog.d(Utils.class, "extractNameFrom: String URL:" + episodeLink);
        MyLog.d(Utils.class, "extractNameFrom: String: " + strings[strings.length - 1]);
        return strings[strings.length - 1];
    }

    public static String getAbsolutePath(String path) {
        return EPISODES_PATH + path;
    }
}
