package com.tapura.podmorecasts.model

import com.google.firebase.database.Exclude
import java.util.*

data class Podcast(

        var title: String = "",
        var author: String = "",
        var summary: String = "",
        @get:Exclude var imagePath: String = "",
        var thumbnailPath: String? = null,
        @get:Exclude var episodes: List<Episode> = Collections.emptyList(),
        var feedUrl: String = "",
        @get:Exclude var isFavorite: Boolean = false
)
