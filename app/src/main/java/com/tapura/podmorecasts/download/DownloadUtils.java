package com.tapura.podmorecasts.download;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tapura.podmorecasts.Utils;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Episode;
import com.tapura.podmorecasts.model.EpisodeMediaState;
import com.tapura.podmorecasts.model.Podcast;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadUtils {

    private static final String TAG = DownloadUtils.class.getSimpleName();

    private static final String ACTION_DOWNLOAD = "download";
    private static final String ACTION_CANCEL = "cancel";

    private static final String EXTRA_FEED = "feed";
    private static final String EXTRA_EPISODE_INDEX = "index";
    private DownloadRequestRepository mRepository;
    private Context mContext;

    public void runCommand(@NonNull Context context, @NonNull Intent intent) {
        mContext = context;
        String action = intent.getAction();
        if (action != null) {
            mRepository = new DownloadRequestRepository(mContext);
            String feed = intent.getStringExtra(EXTRA_FEED);
            final int epiIndex = intent.getIntExtra(EXTRA_EPISODE_INDEX, -1);
            if (TextUtils.isEmpty(feed) && epiIndex == -1) {
                return;
            }
            FirebaseDb db = new FirebaseDb();
            switch (action) {
                case ACTION_DOWNLOAD:
                    db.getPodcast(mContext, feed, new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            startDownload(dataSnapshot.getValue(Podcast.class), epiIndex);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;
                case ACTION_CANCEL:
                    stopDownload(feed, epiIndex);
                    break;
                default:
                    Log.wtf(TAG, "unknown action");
            }
        }
    }

    private void stopDownload(String feed, int epiIndex) {
        long id = mRepository.getId(feed, epiIndex);
        DownloadManager manager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.remove(id);
        }
        mRepository.remove(id);
    }

    private void startDownload(Podcast podcast, int pos) {
        Episode episode = podcast.getEpisodes().get(pos);
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(episode.getEpisodeLink()));
        request.setTitle("Episode download");
        request.setDescription(episode.getTitle());

        // TODO handlePermissions();

        String fileName = Utils.extractNameFrom(episode.getEpisodeLink());

        Log.d("THALES", "onDownloadClick: file name: " + fileName);

        String filePath = Utils.EPISODES_PATH + podcast.getTitle() + File.separator;
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS, filePath + fileName);

        if (downloadManager != null) {
            long refId = downloadManager.enqueue(request);
            DownloadRequest downloadRequest = new DownloadRequest(refId, podcast.getFeedUrl(), pos);
            mRepository.append(downloadRequest);
            FirebaseDb db = new FirebaseDb();
            podcast.getEpisodes().get(pos).setEpisodeState(EpisodeMediaState.DOWNLOADING);
            db.insert(podcast, mContext);
        } else {
            Toast.makeText(mContext, "manager null", Toast.LENGTH_SHORT).show();
        }
    }


    public static Intent createIntentForDownload(@NonNull Context context, @NonNull String feed, int episodePos) {
        Intent i = new Intent(context, DownloadUtils.class);
        i.setAction(ACTION_DOWNLOAD);

        i.putExtra(EXTRA_FEED, feed);
        i.putExtra(EXTRA_EPISODE_INDEX, episodePos);

        return i;
    }

    public static Intent createIntentForCancel(@NonNull Context context, @NonNull String feed, int episodePos) {
        Intent i = new Intent(context, DownloadUtils.class);
        i.setAction(ACTION_CANCEL);

        i.putExtra(EXTRA_FEED, feed);
        i.putExtra(EXTRA_EPISODE_INDEX, episodePos);

        return i;
    }
}
