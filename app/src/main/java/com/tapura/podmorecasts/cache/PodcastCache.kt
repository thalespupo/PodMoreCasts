package com.tapura.podmorecasts.cache

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.tapura.podmorecasts.MyApplication.PODCAST_CACHE
import com.tapura.podmorecasts.model.Podcast


class PodcastCache {

    fun getCache(context: Context?, feed: String): Podcast? {
        val sharedPref = context?.getSharedPreferences(PODCAST_CACHE, Context.MODE_PRIVATE)
        val podcastJson = sharedPref?.getString(feed, "")

        if (!TextUtils.isEmpty(podcastJson)) {
            return Gson().fromJson(podcastJson, Podcast::class.java)
        }

        return null
    }

    fun setCache(context: Context?, podcast: Podcast) {
        val sharedPref = context?.getSharedPreferences(PODCAST_CACHE, Context.MODE_PRIVATE)
        sharedPref?.edit()?.putString(podcast.feedUrl, Gson().toJson(podcast))?.apply()
    }
}