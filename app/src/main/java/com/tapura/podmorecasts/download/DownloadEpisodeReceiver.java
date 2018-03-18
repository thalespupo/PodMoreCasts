package com.tapura.podmorecasts.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.Utils;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.EpisodeMediaState;
import com.tapura.podmorecasts.model.Podcast;

import java.io.File;

public class DownloadEpisodeReceiver extends BroadcastReceiver {

    private DownloadRequestRepository mRepository;

    public DownloadEpisodeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mRepository = new DownloadRequestRepository(context);
        MyLog.d(getClass(), "onReceive");
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            MyLog.d(getClass(), "onReceive: ACTION_DOWNLOAD_COMPLETE");
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            onDownloadCompleted(context, referenceId);
        }
    }

    public void onDownloadCompleted(Context context, long refId) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(refId);

        Cursor c = null;
        if (manager != null) {
            c = manager.query(query);
        }

        DownloadNotification notification = new DownloadNotification();

        if (c != null) {
            if (c.moveToNext()) {
                int downloadStatus = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                MyLog.d(getClass(), "c.moveToNext(): downloadStatus " + downloadStatus);
                notification.createNotification(context, downloadStatus, refId);
                updateDatabase(context, downloadStatus, refId);
                return;
            }
            notification.createNotification(context, DownloadManager.STATUS_FAILED, refId);
            updateDatabase(context, DownloadManager.STATUS_FAILED, refId);
        }
    }

    private void updateDatabase(final Context context, final int downloadStatus, long refId) {
        final FirebaseDb db = new FirebaseDb();
        DownloadRequest request = mRepository.get(refId);
        if (request == null) {
            return;
        }
        final int episodePos = request.getEpisodePos();
        db.getPodcast(context, request.getFeed(), new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Podcast podcast = dataSnapshot.getValue(Podcast.class);
                switch (downloadStatus) {
                    case DownloadManager.STATUS_FAILED:
                        MyLog.d(getClass(), "updateDatabase: FAILED");
                        podcast.getEpisodes().get(episodePos).setEpisodeState(EpisodeMediaState.NOT_IN_DISK);
                        db.insert(podcast, context);
                        mRepository.remove(refId);
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        MyLog.d(getClass(), "updateDatabase: SUCCESSFUL");
                        String filePath = Utils.EPISODES_PATH + podcast.getTitle() + File.separator;
                        String fileName = Utils.extractNameFrom(podcast.getEpisodes().get(episodePos).getEpisodeLink());
                        podcast.getEpisodes().get(episodePos).setPathInDisk(filePath + fileName);
                        podcast.getEpisodes().get(episodePos).setEpisodeState(EpisodeMediaState.COMPLETED);
                        db.insert(podcast, context);
                        mRepository.remove(refId);
                        break;
                    default:
                        // Nothing to do
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
