package com.tapura.podmorecasts.details;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.database.UserControlSharedPrefs;
import com.tapura.podmorecasts.download.DownloadUtils;
import com.tapura.podmorecasts.model.Episode;
import com.tapura.podmorecasts.model.Podcast;
import com.tapura.podmorecasts.model.PodcastKt;
import com.tapura.podmorecasts.player.MediaPlayerActivity;

public class PodcastDetailsActivity extends AppCompatActivity implements EpisodesAdapter.OnDownloadClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_WRITE_PERMISSION = 1;

    private static final String THUMBNAIL_KEY = "thumbnail";
    private static final String FEED_URL_KEY = "feed_url";

    private Podcast mPodcast;
    private RecyclerView mRecyclerView;
    private EpisodesAdapter mAdapter;
    private ProgressBar progressBar;
    private int mSelectedPos;
    private FloatingActionButton fab;
    private String feedUrlFromIntent;
    private String thumbnailFromIntent;

    public static Intent createIntent(Context context, String feedUrl, @Nullable String thumbnail) {
        Intent intent = new Intent(context, PodcastDetailsActivity.class);
        intent.putExtra(FEED_URL_KEY, feedUrl);
        if (thumbnail != null) {
            intent.putExtra(THUMBNAIL_KEY, thumbnail);
        }
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_podcast);

        mRecyclerView = findViewById(R.id.recycler_view_episodes_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mAdapter = new EpisodesAdapter(this);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        fab = findViewById(R.id.fab_add_remove_favorite);

        String feedUrl = getIntent().getStringExtra(FEED_URL_KEY);
        String thumbnail = getIntent().getStringExtra(THUMBNAIL_KEY);

        if (!TextUtils.isEmpty(feedUrl)) {
            this.feedUrlFromIntent = feedUrl;
        }
        if (!TextUtils.isEmpty(thumbnail)) {
            this.thumbnailFromIntent = thumbnail;
        }

        createViewModel(feedUrl);

        startLoadingScheme();
    }

    private void createViewModel(String feedUrl) {
        PodcastDetailsViewModel mModel = ViewModelProviders.of(this).get(PodcastDetailsViewModel.class);

        final Observer<Podcast> observer = new Observer<Podcast>() {
            @Override
            public void onChanged(@Nullable Podcast podcast) {
                if (podcast != null) {
                    mAdapter.isFavorite = podcast.getSourceType() == PodcastKt.SOURCE_TYPE_FIREBASE;
                    bindView(podcast);
                } else {

                    MyLog.e(this.getClass(), "ViewModel onChanged: the podcast info comes null");
                }
            }
        };

        mModel.getCurrentPodcast(feedUrl).observe(this, observer);
    }

    public void onFabClick(View view) {
        FirebaseDb db = new FirebaseDb();
        if (mAdapter.isFavorite) {
            fab.setImageResource(R.drawable.ic_add);
            db.remove(this, mPodcast.getFeedUrl());
            mAdapter.isFavorite = false;
            stopAllDownload();
        } else {
            fab.setImageResource(R.drawable.ic_close);
            boolean result = db.insert(mPodcast, this);

            if (result) {
                Toast.makeText(this, getString(R.string.toast_the_podcast) + mPodcast.getTitle() + getString(R.string.toast_was_added), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_podcast_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void bindView(Podcast podcast) {
        stopLoadingScheme();

        if (podcast == null) {
            Toast.makeText(this, getString(R.string.toast_podcast_error), Toast.LENGTH_SHORT).show();
            MyLog.e(getClass(), "bindview: Podcast is null");
            return;
        }
        if (mAdapter.isFavorite) {
            fab.setImageResource(R.drawable.ic_close);
            fab.setContentDescription(getString(R.string.description_fab_remove_podcast));
        } else {
            fab.setImageResource(R.drawable.ic_add);
            fab.setContentDescription(getString(R.string.description_fab_add_podcast));
        }

        mPodcast = podcast;
        mPodcast.setThumbnailPath(thumbnailFromIntent);
        mPodcast.setFeedUrl(feedUrlFromIntent);

        Toolbar toolbar = findViewById(R.id.toolbar);
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
        fab.setVisibility(View.VISIBLE);

        mRecyclerView.setVisibility(View.VISIBLE);

        progressBar = findViewById(R.id.layout_loading_progressbar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDownloadClick(int pos) {
        Episode episode = mPodcast.getEpisodes().get(pos);

        switch (episode.getEpisodeState()) {
            case DOWNLOADING:
                stopDownload(pos);
                break;
            case NOT_IN_DISK:
                startDownload(pos);
                break;
            case COMPLETED:
                startActivity(MediaPlayerActivity.createIntent(this, mPodcast.getFeedUrl(), pos));
                finish();
        }
    }

    private void startDownload(int pos) {
        mSelectedPos = pos;
        requestPermission();
    }

    private void stopDownload(int pos) {
        DownloadUtils utils = new DownloadUtils(this);
        utils.stopDownload(mPodcast.getFeedUrl(), pos);
    }

    private void stopAllDownload() {
        DownloadUtils utils = new DownloadUtils(this);
        utils.stopDownload(mPodcast.getFeedUrl(), -1);
        mRecyclerView.setEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new DownloadUtils(this).startDownload(mPodcast, mSelectedPos);
        }
    }

    private void requestPermission() {
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
    }

    @Override
    protected void onResume() {
        if (!UserControlSharedPrefs.isUserLogged(this)) {
            finish();
        }
        super.onResume();
    }
}
