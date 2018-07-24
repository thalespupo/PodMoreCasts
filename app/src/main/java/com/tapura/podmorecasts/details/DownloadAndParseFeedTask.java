package com.tapura.podmorecasts.details;


import android.os.AsyncTask;

import com.tapura.podmorecasts.MyApplication;
import com.tapura.podmorecasts.Utils;
import com.tapura.podmorecasts.model.Podcast;
import com.tapura.podmorecasts.parser.FeedParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class DownloadAndParseFeedTask extends AsyncTask<String, Integer, Podcast> {

    public interface CallBack {
        void onDownloadAndParseFinished(Podcast podcast);
    }

    private CallBack mCallBack;

    public DownloadAndParseFeedTask(CallBack callBack) {
        mCallBack = callBack;
    }

    @Override
    protected Podcast doInBackground(String... strings) {

        InputStream inputStream = null;
        try {
            inputStream = Utils.downloadXml(strings[0], MyApplication.getApp());
        } catch (IOException e) {
            e.printStackTrace();
        }

        FeedParser parser = new FeedParser();
        Podcast podcast = null;
        try {
            podcast = parser.parse(inputStream);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return podcast;
    }

    @Override
    protected void onPostExecute(Podcast podcast) {
        mCallBack.onDownloadAndParseFinished(podcast);
    }
}
