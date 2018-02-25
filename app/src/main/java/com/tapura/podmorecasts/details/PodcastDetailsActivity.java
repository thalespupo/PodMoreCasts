package com.tapura.podmorecasts.details;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Podcast;

import static com.tapura.podmorecasts.main.MainActivity.FAVORITE_KEY;
import static com.tapura.podmorecasts.main.MainActivity.FEED_URL_KEY;
import static com.tapura.podmorecasts.main.MainActivity.THUMBNAIL_KEY;

public class PodcastDetailsActivity extends AppCompatActivity implements FirebaseDb.PodcastFromFirebaseListener {


    private Podcast mPodcast;
    private RecyclerView mRecyclerView;
    private EpisodesAdapter mAdapter;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private PodcastDetailsViewModel mModel;
    private boolean isFavorite = false;

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

        isFavorite = getIntent().getBooleanExtra(FAVORITE_KEY, false);
        String feedUrl = getIntent().getStringExtra(FEED_URL_KEY);
        String thumbnail = getIntent().getStringExtra(THUMBNAIL_KEY);
        mPodcast = new Podcast();
        if (!TextUtils.isEmpty(feedUrl)) {
            mPodcast.setFeedUrl(feedUrl);
        }
        if (!TextUtils.isEmpty(thumbnail)) {
            mPodcast.setThumbnailPath(thumbnail);
        }

        mAdapter.isFavorite = isFavorite;

        if (isFavorite) {
            loadFavorite(feedUrl);
        } else {
            loadPodcastFeed(feedUrl);
        }
        startLoadingScheme();
    }

    private void loadFavorite(String feedUrl) {
        FirebaseDb.getPodcast(this, feedUrl, this);
    }

    private void loadPodcastFeed(String feedUrl) {
        // Livedata
        mModel = ViewModelProviders.of(this).get(PodcastDetailsViewModel.class);

        final Observer<Podcast> observer = new Observer<Podcast>() {
            @Override
            public void onChanged(@Nullable Podcast podcast) {
                bindView(podcast);
            }
        };

        mModel.getCurrentPodcast(this, feedUrl).observe(this, observer);

        startLoadingScheme();
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
        bindView(podcast);
    }
}
