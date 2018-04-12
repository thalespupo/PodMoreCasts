package com.tapura.podmorecasts.player;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tapura.podmorecasts.MyApplication;
import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.Utils;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Episode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class MediaPlayerActivity extends AppCompatActivity {

    private static final String EXTRA_FEED_URL = "extra_feed_url";
    private static final String EXTRA_EPISODE_POS = "extra_episode_pos";
    private static final String SIS_BOUND = "sis_bound";
    private FirebaseDb mDb;
    private Episode mEpisode;
    private String mFeed;
    private int mEpisodePos;
    private File mMedia;
    private final ValueEventListener fbCallback = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                mEpisode = dataSnapshot.getValue(Episode.class);
                initializeUi();
                initializeService();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private SimpleExoPlayerView mPlayerView;
    private Bitmap mOutdoor;
    private MediaPlayerService mService;

    public static Intent createIntent(Context context, String feedUrl, int pos) {
        Intent intent = new Intent(context, MediaPlayerActivity.class);
        intent.putExtra(EXTRA_FEED_URL, feedUrl);
        intent.putExtra(EXTRA_EPISODE_POS, pos);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        mFeed = getIntent().getStringExtra(EXTRA_FEED_URL);
        mEpisodePos = getIntent().getIntExtra(EXTRA_EPISODE_POS, -1);

        mDb = new FirebaseDb();
        mDb.attachEpisodeListener(MyApplication.getApp(), mFeed, mEpisodePos, fbCallback);

        mPlayerView = findViewById(R.id.playerView);

        if (savedInstanceState != null) {
            bound = savedInstanceState.getBoolean(SIS_BOUND);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDb != null) {
            mDb.detachEpisodeListener(MyApplication.getApp(), mFeed, mEpisodePos, fbCallback);
        }
        if (bound) {
            unbindService(mConnection);
        }
    }

    private void initializeUi() {
        mMedia = new File(Utils.getAbsolutePath(mEpisode.getPathInDisk()));
        MyLog.d(getClass(), "initializeUi: media path=" + mMedia.getAbsolutePath());

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(mMedia.getAbsolutePath());
        InputStream inputStream = null;
        if (mmr.getEmbeddedPicture() != null) {
            inputStream = new ByteArrayInputStream(mmr.getEmbeddedPicture());
        }
        mmr.release();

        mOutdoor = BitmapFactory.decodeStream(inputStream);
        mPlayerView.setDefaultArtwork(mOutdoor);
    }

    private void initializeService() {
        MyLog.d(getClass(), "initializeService");
        Intent intent = new Intent(this, MediaPlayerService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private boolean bound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLog.d(getClass(), "onServiceConnected");
            bound = true;
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mService = binder.getService();

            if (!mService.wasInitialized()) {
                mService.initializeService(mEpisode, mOutdoor);
            }

            mPlayerView.setPlayer(mService.getPlayer());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SIS_BOUND, bound);
        super.onSaveInstanceState(outState);
    }
}