package com.tapura.podmorecasts.player;


import com.tapura.podmorecasts.model.Episode;

public interface EpisodeListener {
    void onLoadEpisode(Episode episode);
    void onError();
}
