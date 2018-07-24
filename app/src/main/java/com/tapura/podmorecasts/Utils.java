package com.tapura.podmorecasts;


import android.content.Context;
import android.os.Environment;

import com.readystatesoftware.chuck.ChuckInterceptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {
    public static final String EPISODES_PATH = File.separator + MyApplication.getApp().getPackageName() + File.separator;

    public static String extractNameFrom(String episodeLink) {
        String[] strings = episodeLink.split("/");
        MyLog.d(Utils.class, "extractNameFrom: String URL:" + episodeLink);
        MyLog.d(Utils.class, "extractNameFrom: String: " + strings[strings.length - 1]);
        return strings[strings.length - 1];
    }

    public static String getAbsolutePath(String path) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS) + path;
    }

    public static InputStream downloadXml(String url, Context context) throws IOException {
        OkHttpClient client = createClient(context);
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().byteStream();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static OkHttpClient createClient(Context context) {
        return new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(context))
                .build();
    }
}
