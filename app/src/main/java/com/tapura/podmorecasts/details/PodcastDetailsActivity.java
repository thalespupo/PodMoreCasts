package com.tapura.podmorecasts.details;


import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Episode;
import com.tapura.podmorecasts.model.Podcast;

import java.io.File;

import static com.tapura.podmorecasts.main.MainActivity.FEED_URL_KEY;
import static com.tapura.podmorecasts.main.MainActivity.THUMBNAIL_KEY;

public class PodcastDetailsActivity extends AppCompatActivity implements FirebaseDb.PodcastFromFirebaseListener, EpisodesAdapter.OnDownloadClickListener {


    private Podcast mPodcast;
    private RecyclerView mRecyclerView;
    private EpisodesAdapter mAdapter;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private PodcastDetailsViewModel mModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_podcast);

        mRecyclerView = findViewById(R.id.recycler_view_episodes_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mAdapter = new EpisodesAdapter(this, this);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        String feedUrl = getIntent().getStringExtra(FEED_URL_KEY);
        String thumbnail = getIntent().getStringExtra(THUMBNAIL_KEY);
        mPodcast = new Podcast();
        if (!TextUtils.isEmpty(feedUrl)) {
            mPodcast.setFeedUrl(feedUrl);
        }
        if (!TextUtils.isEmpty(thumbnail)) {
            mPodcast.setThumbnailPath(thumbnail);
        }

        tryToLoadFavorite(feedUrl);

        startLoadingScheme();
    }

    private void tryToLoadFavorite(String feedUrl) {
        FirebaseDb.getPodcast(this, feedUrl, this);
    }

    private void loadPodcastFeed(String feedUrl) {
        // Livedata
        mModel = ViewModelProviders.of(this).get(PodcastDetailsViewModel.class);

        final Observer<Podcast> observer = new Observer<Podcast>() {
            @Override
            public void onChanged(@Nullable Podcast podcast) {
                mAdapter.isFavorite = false;
                bindView(podcast);
            }
        };

        mModel.getCurrentPodcast(this, feedUrl).observe(this, observer);

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
        stopLoadingScheme();

        if (podcast == null) {
            Toast.makeText(this, "Podcast Null!!", Toast.LENGTH_LONG).show();
            return;
        }
        podcast.setFeedUrl(mPodcast.getFeedUrl());
        podcast.setThumbnailPath(mPodcast.getThumbnailPath());
        mPodcast = podcast;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mPodcast.getTitle());

        ImageView ivThumbnail = findViewById(R.id.thumbnail);

        Picasso.with(this)
                .load(mPodcast.getImagePath())
                .into(ivThumbnail);

        mAdapter.setList(mPodcast.getEpisodes());
    }

    private void startLoadingScheme() {
        progressBar = findViewById(R.id.layout_loading_progressbar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopLoadingScheme() {
        fab = findViewById(R.id.fab_add_favorite);
        fab.setVisibility(View.VISIBLE);

        mRecyclerView.setVisibility(View.VISIBLE);

        progressBar = findViewById(R.id.layout_loading_progressbar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoadedPodcast(Podcast podcast) {
        if (podcast == null) {
            // We don't have any podcast in firebase, so it is not a favorite podcast
            // The podcast will be search by the feed URL
            loadPodcastFeed(mPodcast.getFeedUrl());
            return;
        }
        mAdapter.isFavorite = true;
        bindView(podcast);
    }

    @Override
    public void onDownloadClick(int pos, EpisodesAdapter.DownloadListener listener) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Episode episode = mPodcast.getEpisodes().get(pos);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(episode.getEpisodeLink()));
        request.setTitle("Episode download");
        request.setDescription(episode.getTitle());

        handlePermissions();

        String fileName = extractNameFrom(episode.getEpisodeLink());

        Log.d("THALES", "onDownloadClick: file name: " + fileName);

        String filePath = File.separator + getPackageName() + File.separator + mPodcast.getTitle() + File.separator;
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS, filePath + fileName);

        if (downloadManager != null) {
            downloadManager.enqueue(request);
        } else {
            Toast.makeText(this, "manager null", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * We will split the episode linke, and get the last part with '.mp3' suffix
     *
     * @param episodeLink
     * @return file name
     */
    private String extractNameFrom(String episodeLink) {
        String[] strings = episodeLink.split("/");

        Log.d("THALES", "extractNameFrom: String URL:" + episodeLink);
        for (String s : strings) {
            Log.d("THALES", "extractNameFrom: String: " + s);
        }

        return strings[strings.length - 1];
    }

    private void handlePermissions() {
        // TODO
    }

}
