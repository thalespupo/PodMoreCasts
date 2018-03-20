package com.tapura.podmorecasts.player;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tapura.podmorecasts.model.Episode;

public class EpisodeValueEventList implements ValueEventListener {

    private String feedUrl;
    private int pos;
    private EpisodeListener mCallback;

    public EpisodeValueEventList(String feedUrl, int pos, EpisodeListener listener) {
        this.feedUrl = feedUrl;
        this.pos = pos;
        this.mCallback = listener;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            mCallback.onLoadEpisode(dataSnapshot.getValue(Episode.class));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        mCallback.onError();
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public int getPos() {
        return pos;
    }
}
