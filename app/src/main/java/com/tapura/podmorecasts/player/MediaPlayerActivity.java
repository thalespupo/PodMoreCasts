package com.tapura.podmorecasts.player;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tapura.podmorecasts.MyApplication;
import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.Utils;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.details.PodcastDetailsActivity;
import com.tapura.podmorecasts.model.Episode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MediaPlayerActivity extends AppCompatActivity {

    private static final String EXTRA_FEED_URL = "extra_feed_url";
    private static final String EXTRA_EPISODE_POS = "extra_episode_pos";
    private FirebaseDb mDb;
    private Episode mEpisode;
    private String mFeed;
    private int mEpisodePos;
    private ValueEventListener fbCallback = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                mEpisode = dataSnapshot.getValue(Episode.class);
                initializeUi();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;


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
    }

    @Override
    protected void onDestroy() {
        if (mDb != null) {
            mDb.detachEpisodeListener(MyApplication.getApp(), mFeed, mEpisodePos, fbCallback);
        }
        super.onDestroy();
    }

    private void initializeUi() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(Utils.getAbsolutePath(mEpisode.getPathInDisk()));
        InputStream inputStream = null;
        if (mmr.getEmbeddedPicture() != null) {
            inputStream = new ByteArrayInputStream(mmr.getEmbeddedPicture());
        }
        mmr.release();

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        mPlayerView.setDefaultArtwork(bitmap);
    }

}