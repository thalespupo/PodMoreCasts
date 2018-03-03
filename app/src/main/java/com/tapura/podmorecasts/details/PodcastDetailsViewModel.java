package com.tapura.podmorecasts.details;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.readystatesoftware.chuck.ChuckInterceptor;
import com.tapura.podmorecasts.MyApplication;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Podcast;
import com.tapura.podmorecasts.parser.FeedParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PodcastDetailsViewModel extends ViewModel implements ValueEventListener {

    private static final String TAG = PodcastDetailsViewModel.class.getCanonicalName();

    // Pair is a Podcast object, and the source type: true = Firebase, false = DownloadTask
    private MutableLiveData<Pair<Podcast, Boolean>> mCurrentPodcast;
    private FirebaseDb firebaseDb;
    private String currentFeed;

    public MutableLiveData<Pair<Podcast, Boolean>> getCurrentPodcast(String feed) {
        if (mCurrentPodcast == null) {
            mCurrentPodcast = new MutableLiveData<>();
            firebaseDb = getFirebaseDatabaseInstance();
            firebaseDb.attachPodcastListener(MyApplication.getApp(), feed, this);
        }
        currentFeed = feed;
        return mCurrentPodcast;
    }

    private FirebaseDb getFirebaseDatabaseInstance() {
        if (firebaseDb == null) {
            firebaseDb = new FirebaseDb();
        }
        return firebaseDb;
    }

    private void onLoadedPodcast(Podcast podcast) {
        mCurrentPodcast.setValue(new Pair<>(podcast, true));
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            onLoadedPodcast(dataSnapshot.getValue(Podcast.class));
        } else {
            new DownloadAndParseFeedTask(MyApplication.getApp()).execute(currentFeed);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        new DownloadAndParseFeedTask(MyApplication.getApp()).execute(currentFeed);
    }

    @Override
    protected void onCleared() {
        if (firebaseDb != null) {
            firebaseDb.detachPodcastListener(MyApplication.getApp(), currentFeed, this);
        }
        super.onCleared();
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
            mCurrentPodcast.setValue(new Pair<>(podcast, false));
        }

        private OkHttpClient createClient(Context mContext) {
            return new OkHttpClient.Builder()
                    .addInterceptor(new ChuckInterceptor(mContext))
                    .build();
        }
    }
}
