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
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Podcast;
import com.tapura.podmorecasts.model.PodcastKt;

public class PodcastDetailsViewModel extends ViewModel implements ValueEventListener, DownloadAndParseFeedTask.CallBack {

    private MutableLiveData<Podcast> mCurrentPodcast;
    private FirebaseDb firebaseDb;
    private String currentFeed;
    private boolean wasLoaded;

    public MutableLiveData<Podcast> getCurrentPodcast(String feed) {
        if (mCurrentPodcast == null) {
            mCurrentPodcast = new MutableLiveData<>();
            wasLoaded = false;
            firebaseDb = getFirebaseDatabaseInstance();
            firebaseDb.attachPodcastListener(MyApplication.getApp(), feed, this);
        }
        currentFeed = feed;
        return mCurrentPodcast;
    }

    private FirebaseDb getFirebaseDatabaseInstance() {
        if (firebaseDb == null) {
            firebaseDb = new FirebaseDb();
        }
        return firebaseDb;
    }

    private void onLoadedPodcast(Podcast podcast) {
        podcast.setSourceType(PodcastKt.SOURCE_TYPE_FIREBASE);
        mCurrentPodcast.setValue(podcast);
        wasLoaded = true;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        MyLog.d(getClass(), "onDataChange: dataSnapshot" + dataSnapshot);
        if (dataSnapshot.exists()) {
            onLoadedPodcast(dataSnapshot.getValue(Podcast.class));
        } else if (wasLoaded) {
            Podcast alreadyLoaded = mCurrentPodcast.getValue();
            if (alreadyLoaded != null) {
                mCurrentPodcast.setValue(alreadyLoaded);
            }
        } else {
            new DownloadAndParseFeedTask(this).execute(currentFeed);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(MyApplication.getApp(),
                "Error in database: " + databaseError.getCode() + " " + databaseError.getMessage(),
                Toast.LENGTH_LONG).show();
        MyLog.e(getClass(), databaseError.getDetails());
    }

    @Override
    protected void onCleared() {
        if (firebaseDb != null) {
            firebaseDb.detachPodcastListener(MyApplication.getApp(), currentFeed, this);
        }
        super.onCleared();
    }

    @Override
    public void onDownloadAndParseFinished(Podcast podcast) {
        mCurrentPodcast.setValue(podcast);
        wasLoaded = true;
    }
}
