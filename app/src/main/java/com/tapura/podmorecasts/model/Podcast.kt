package com.tapura.podmorecasts.model

import java.util.*

data class Podcast(
        var title: String = "",
        var author: String = "",
        var summary: String = "",
        var imagePath: String = "",
        var thumbnailPath: String? = null,
        var episodes: List<Episode> = Collections.emptyList(),
        var feedUrl: String? = null
)
