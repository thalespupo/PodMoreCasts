package com.tapura.podmorecasts.details;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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

import static com.tapura.podmorecasts.discover.DiscoverPodcastFragment.FEED_URL_KEY;

public class PodcastDetailsActivity extends AppCompatActivity {

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

        mAdapter = new EpisodesAdapter();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        String feedUrl = getIntent().getStringExtra(FEED_URL_KEY);
        mPodcast = new Podcast();
        if (!TextUtils.isEmpty(feedUrl)) {
            mPodcast.setFeedUrl(feedUrl);
        }

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
}
