package com.tapura.podmorecasts.download;


import android.support.annotation.NonNull;

public class DownloadRequest {

    private final long mRefId;
    private final String mFeed;
    private final int mEpisodePos;

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
