package com.tapura.podmorecasts.download;


public interface EpisodeDownloadListener {
    void onCancelDownload(int epiIndex);
    void onCancelAllDownload();
    void onStartDownload(int epiIndex);
}
