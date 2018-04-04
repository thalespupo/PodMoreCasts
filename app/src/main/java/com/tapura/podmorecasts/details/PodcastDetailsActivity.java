package com.tapura.podmorecasts.details;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
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
import com.tapura.podmorecasts.download.DownloadUtils;
import com.tapura.podmorecasts.model.Episode;
import com.tapura.podmorecasts.model.Podcast;
import com.tapura.podmorecasts.player.MediaPlayerActivity;

import static com.tapura.podmorecasts.main.MainActivity.FEED_URL_KEY;
import static com.tapura.podmorecasts.main.MainActivity.THUMBNAIL_KEY;

public class PodcastDetailsActivity extends AppCompatActivity implements EpisodesAdapter.OnDownloadClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_WRITE_PERMISSION = 1;

    private Podcast mPodcast;
    private RecyclerView mRecyclerView;
    private EpisodesAdapter mAdapter;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private PodcastDetailsViewModel mModel;
    private int mSelectedPos;

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

        String feedUrl = getIntent().getStringExtra(FEED_URL_KEY);
        String thumbnail = getIntent().getStringExtra(THUMBNAIL_KEY);
        mPodcast = new Podcast();
        if (!TextUtils.isEmpty(feedUrl)) {
            mPodcast.setFeedUrl(feedUrl);
        }
        if (!TextUtils.isEmpty(thumbnail)) {
            mPodcast.setThumbnailPath(thumbnail);
        }

        createViewModel(feedUrl);

        startLoadingScheme();
    }

    private void createViewModel(String feedUrl) {
        mModel = ViewModelProviders.of(this).get(PodcastDetailsViewModel.class);

        final Observer<Pair<Podcast, Boolean>> observer = new Observer<Pair<Podcast, Boolean>>() {
            @Override
            public void onChanged(@Nullable Pair<Podcast, Boolean> pair) {
                if (pair != null) {
                    mAdapter.isFavorite = pair.second;
                    bindView(pair.first);
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
            db.remove(this, mPodcast.getFeedUrl());
            mAdapter.isFavorite = false;
            stopAllDownload();
        } else {
            boolean result = db.insert(mPodcast, this);

            if (result) {
                Toast.makeText(this, "The Podcast " + mPodcast.getTitle() + " was added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error, please check!!", Toast.LENGTH_SHORT).show();
            }
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
        fab = findViewById(R.id.fab_add_favorite);
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

    private void openFile(Episode episode) {
        MediaPlayer mp = new MediaPlayer();

        try {
            mp.setDataSource(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS) + episode.getPathInDisk());
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
