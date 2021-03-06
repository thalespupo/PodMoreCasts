package com.tapura.podmorecasts.download;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.tapura.podmorecasts.MyApplication;
import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.Utils;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Episode;
import com.tapura.podmorecasts.model.EpisodeKt;
import com.tapura.podmorecasts.model.EpisodeMediaState;
import com.tapura.podmorecasts.model.Podcast;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadUtils {

    private final DownloadRequestRepository mRepository;
    private final Context mContext;

    public DownloadUtils(Context context) {
        mContext = context;
        mRepository = new DownloadRequestRepository(mContext);
    }

    public void stopDownload(String feed, int epiIndex) {
        DownloadManager manager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        if (manager != null) {
            if (epiIndex == -1) { // cancelAll
                long[] ids = mRepository.getAllId(feed);
                if (ids != null && ids.length > 0) {
                    manager.remove(ids);
                    mRepository.remove(ids);
                }
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
        request.setTitle(mContext.getString(R.string.download_title));
        request.setDescription(episode.getTitle());

        String fileName = Utils.extractNameFrom(episode.getEpisodeLink());

        MyLog.d(getClass(), "onDownloadClick: file name: " + fileName);

        String filePath = Utils.EPISODES_PATH + podcast.getTitle() + File.separator;
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS, filePath + fileName);

        if (downloadManager != null) {
            long refId = downloadManager.enqueue(request);
            DownloadRequest downloadRequest = new DownloadRequest(refId, podcast.getFeedUrl(), pos);
            mRepository.append(downloadRequest);
            FirebaseDb db = new FirebaseDb();
            podcast.getEpisodes().get(pos).setEpisodeState(EpisodeKt.STATE_DOWNLOADING);
            //db.insert(podcast, mContext);
        } else {
            Toast.makeText(mContext,
                    mContext.getString(R.string.toast_download_error),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
