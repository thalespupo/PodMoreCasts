package com.tapura.podmorecasts.details;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.readystatesoftware.chuck.ChuckInterceptor;
import com.tapura.podmorecasts.model.Episode;
import com.tapura.podmorecasts.model.Podcast;
import com.tapura.podmorecasts.parser.FeedParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PodcastDetailsViewModel extends ViewModel {

    private MutableLiveData<Podcast> mCurrentPodcast;
    private Podcast mPodcast;

    public MutableLiveData<Podcast> getCurrentPodcast(Context context, String feed) {
        if (mCurrentPodcast == null) {
            mCurrentPodcast = new MutableLiveData<>();
            mPodcast = new Podcast();
            mPodcast.setEpisodes(new ArrayList<Episode>());
            loadPodcastFeed(context.getApplicationContext(), feed);
        }
        return mCurrentPodcast;
    }

    private void loadPodcastFeed(Context applicationContext, String feed) {
        new DownloadAndParseFeedTask(applicationContext).execute(feed);
    }

    public void onPodcastLoading(Pair<Object, FeedParser.PodcastItemType> item) {
        if (item != null && item.second != null) {
            switch (item.second) {
                case TITLE:
                    mPodcast.setTitle((String) item.first);
                    break;
                case SUMMARY:
                    mPodcast.setSummary((String) item.first);
                    break;
                case IMAGE:
                    mPodcast.setImagePath((String) item.first);
                    break;
                case AUTHOR:
                    mPodcast.setAuthor((String) item.first);
                    break;
                case EPISODE:
                    mPodcast.getEpisodes().add((Episode) item.first);
                    break;
            }

            mCurrentPodcast.setValue(mPodcast);
        }
    }

    public class DownloadAndParseFeedTask extends AsyncTask<String, Pair<Object, FeedParser.PodcastItemType>, Boolean> implements FeedParser.PodcastParserListener {
        private final Context mContext;

        public DownloadAndParseFeedTask(Context applicationContext) {
            mContext = applicationContext;
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            InputStream inputStream = null;
            try {
                inputStream = downloadXml(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //nada
            publishProgress();
            FeedParser parser = new FeedParser(this);

            try {
                parser.parse(inputStream);
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return true;
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
        protected void onPostExecute(Boolean response) {
            // TODO handle the full download/parse of the podcast
        }

        @Override
        public void onItemLoaded(Object o, FeedParser.PodcastItemType itemType) {
            publishProgress(new Pair<>(o, itemType));
        }



        @Override
        protected void onProgressUpdate(Pair<Object, FeedParser.PodcastItemType>... values) {
            PodcastDetailsViewModel.this.onPodcastLoading(values[0]);
        }
    }


    private OkHttpClient createClient(Context mContext) {
        return new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(mContext))
                .build();
    }
}
