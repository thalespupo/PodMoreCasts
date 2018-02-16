package com.tapura.podmorecasts.details;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.readystatesoftware.chuck.ChuckInterceptor;
import com.squareup.picasso.Picasso;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Podcast;
import com.tapura.podmorecasts.parser.FeedParser;

import org.parceler.Parcels;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.tapura.podmorecasts.discover.DiscoverPodcastActivity.FEED_URL_KEY;

public class PodcastDetailsActivity extends AppCompatActivity {

    private static final String PODCAST_KEY = "podcast";
    private static final String FEED_LOADED_KEY = "feed_loaded";
    private Podcast mPodcast;
    private RecyclerView mRecyclerView;
    private EpisodesAdapter mAdapter;

    private boolean mFeedLoaded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_podcast);

        mRecyclerView = findViewById(R.id.recycler_view_episodes_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mAdapter = new EpisodesAdapter();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        String feedUrl = getIntent().getStringExtra(FEED_URL_KEY);
        mPodcast = new Podcast();
        if (!TextUtils.isEmpty(feedUrl)) {
            mPodcast.setFeedUrl(feedUrl);
        }

        if (savedInstanceState != null) {
            onResumeRotate(savedInstanceState);
        } else {
            new DownloadAndParseFeedTask().execute(feedUrl);
        }
    }

    public void favoritePodcast(View view) {
        boolean result = FirebaseDb.insert(mPodcast, this);

        if (result) {
            Toast.makeText(this, "The Podcast " + mPodcast.getTitle() + " was added!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error, please check!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void bindView(Podcast podcast) {
        if (podcast == null) {
            Toast.makeText(this, "Podcast Null!!", Toast.LENGTH_LONG).show();
            return;
        }
        mFeedLoaded = true;
        podcast.setFeedUrl(mPodcast.getFeedUrl());
        mPodcast = podcast;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mPodcast.getTitle());

        ImageView ivThumbnail = findViewById(R.id.thumbnail);

        Picasso.with(this)
                .load(mPodcast.getImagePath())
                .into(ivThumbnail);

        //TextView tvAuthor = findViewById(R.id.text_view_podcast_author);
        //TextView tvSummary = findViewById(R.id.text_view_podcast_summary);

        //tvAuthor.setText(mPodcast.getAuthor());
        //tvSummary.setText(mPodcast.getSummary());

        mAdapter.setList(mPodcast.getEpisodes());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(PODCAST_KEY, Parcels.wrap(mPodcast));
        outState.putBoolean(FEED_LOADED_KEY, mFeedLoaded);
        super.onSaveInstanceState(outState);
    }

    private void onResumeRotate(Bundle bundle) {
        mPodcast = Parcels.unwrap(bundle.getParcelable(PODCAST_KEY));
        mFeedLoaded = bundle.getBoolean(FEED_LOADED_KEY);
        if (mFeedLoaded) {
            bindView(mPodcast);
        } else {
            new DownloadAndParseFeedTask().execute(mPodcast.getFeedUrl());
        }

    }

    public class DownloadAndParseFeedTask extends AsyncTask<String, Integer, Podcast> {
        @Override
        protected Podcast doInBackground(String... strings) {

            InputStream inputStream = null;//getApplicationContext().getResources().openRawResource(R.raw.fakedata);
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
            OkHttpClient client = createClient();
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
            bindView(podcast);
        }
    }

    private OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(this))
                .build();
    }

}
