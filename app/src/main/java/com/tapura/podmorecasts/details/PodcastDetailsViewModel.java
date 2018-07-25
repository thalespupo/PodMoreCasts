package com.tapura.podmorecasts.details;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Pair;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tapura.podmorecasts.MyApplication;
import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.cache.PodcastCache;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Podcast;


import java.util.Objects;

public class PodcastDetailsViewModel extends ViewModel implements DownloadAndParseFeedTask.CallBack {

    private MutableLiveData<Podcast> mCurrentPodcast;
    private Podcast cachedPodcast;

    public MutableLiveData<Podcast> getCurrentPodcast(String feed) {
        if (mCurrentPodcast == null) {
            mCurrentPodcast = new MutableLiveData<>();

            cachedPodcast = new PodcastCache().getCache(MyApplication.getApp(), feed);
            if (cachedPodcast == null) {
                new DownloadAndParseFeedTask(this).execute(feed);
            }
            mCurrentPodcast.setValue(cachedPodcast);
        }
        return mCurrentPodcast;
    }

    @Override
    public void onDownloadAndParseFinished(Podcast podcast) {
        cachedPodcast = podcast;
        new PodcastCache().setCache(MyApplication.getApp(), podcast);
        mCurrentPodcast.setValue(cachedPodcast);
    }
}
