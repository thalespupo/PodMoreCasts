package com.tapura.podmorecasts.model

import com.google.firebase.database.Exclude
import java.util.*

const val SOURCE_TYPE_FEED = 0
const val SOURCE_TYPE_FIREBASE = 1
data class Podcast(

        var title: String = "",
        var author: String = "",
        var summary: String = "",
        var imagePath: String = "",
        var thumbnailPath: String? = null,
        var episodes: List<Episode> = Collections.emptyList(),
        var feedUrl: String? = null,
        @Exclude var sourceType: Int = SOURCE_TYPE_FEED
)
