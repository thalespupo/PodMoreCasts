package com.tapura.podmorecasts.details;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;

import com.readystatesoftware.chuck.ChuckInterceptor;
import com.tapura.podmorecasts.model.Podcast;
import com.tapura.podmorecasts.parser.FeedParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PodcastDetailsViewModel extends ViewModel {

    private MutableLiveData<Podcast> mCurrentPodcast;

    public MutableLiveData<Podcast> getCurrentPodcast(Context context, String feed) {
        if (mCurrentPodcast == null) {
            mCurrentPodcast = new MutableLiveData<>();
            loadPodcastFeed(context.getApplicationContext(), feed);
        }
        return mCurrentPodcast;
    }

    private void loadPodcastFeed(Context applicationContext, String feed) {
        new DownloadAndParseFeedTask(applicationContext).execute(feed);
    }

    public class DownloadAndParseFeedTask extends AsyncTask<String, Integer, Podcast> {
        private final Context mContext;

        public DownloadAndParseFeedTask(Context applicationContext) {
            mContext = applicationContext;
        }

        @Override
        protected Podcast doInBackground(String... strings) {

            InputStream inputStream = null;
            try {
                inputStream = downloadXml(strings[0]);
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

        private InputStream downloadXml(String url) throws IOException {
            OkHttpClient client = createClient(mContext);
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

        @Override
        protected void onPostExecute(Podcast podcast) {
            mCurrentPodcast.setValue(podcast);
        }
    }

    private OkHttpClient createClient(Context mContext) {
        return new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(mContext))
                .build();
    }
}
