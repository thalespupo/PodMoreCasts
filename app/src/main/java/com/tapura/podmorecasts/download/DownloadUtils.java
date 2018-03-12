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

    private DownloadRequestRepository mRepository;
    private Context mContext;

    public DownloadUtils(Context context) {
        mContext = context;
        mRepository = new DownloadRequestRepository(mContext);
    }

    public void stopDownload(String feed, int epiIndex) {
        DownloadManager manager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        if (manager != null) {
            if (epiIndex == -1) { // cancelAll
                long[] ids = mRepository.getAllId(feed);
                manager.remove(ids);
                mRepository.remove(ids);
            } else {
                long id = mRepository.getId(feed, epiIndex);
                manager.remove(id);
            }
        }

    }

    public void startDownload(Podcast podcast, int pos) {
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
}
