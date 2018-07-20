package com.tapura.podmorecasts.model

data class Episode(
        var title: String = "",
        var link: String = "",
        var episodeLink: String = "",
        var description: String = "",
        var pathInDisk: String? = null,
        var episodeState: EpisodeMediaState = EpisodeMediaState.NOT_IN_DISK
)