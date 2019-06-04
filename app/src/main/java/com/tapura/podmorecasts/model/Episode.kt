package com.tapura.podmorecasts.model


const val STATE_NOT_IN_DISK = 0
const val STATE_DOWNLOADING = 1
const val STATE_DOWNLOADED = 2
data class Episode(
        var title: String = "",
        var link: String = "",
        var episodeLink: String = "",
        var description: String = "",
        var pathInDisk: String? = null,
        var guid: String = "",
        var episodeState: Int = STATE_NOT_IN_DISK
)