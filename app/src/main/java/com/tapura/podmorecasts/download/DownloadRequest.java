package com.tapura.podmorecasts.download;


import android.support.annotation.NonNull;

public class DownloadRequest {

    private long mRefId;
    private String mFeed;
    private int mEpisodePos;

    public DownloadRequest(long refId, @NonNull String feed, int episodePos) {
        this.mRefId = refId;
        this.mFeed = feed;
        this.mEpisodePos = episodePos;
    }

    public long getRefId() {
        return mRefId;
    }

    public String getFeed() {
        return mFeed;
    }

    public int getEpisodePos() {
        return mEpisodePos;
    }
}
