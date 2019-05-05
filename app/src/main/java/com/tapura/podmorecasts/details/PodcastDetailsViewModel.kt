package com.tapura.podmorecasts.details


import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.tapura.podmorecasts.MyApplication
import com.tapura.podmorecasts.cache.PodcastCache
import com.tapura.podmorecasts.model.Podcast

class PodcastDetailsViewModel : ViewModel() {

    private lateinit var mCurrentPodcast: MutableLiveData<Podcast>
    private var cachedPodcast: Podcast? = null

    fun getCurrentPodcast(feed: String): MutableLiveData<Podcast> {
        if (::mCurrentPodcast.isInitialized.not()) {
            mCurrentPodcast = MutableLiveData()

            cachedPodcast = PodcastCache().getCache(MyApplication.getApp(), feed)

            if (cachedPodcast == null) {
                Log.d("thales", "cache null")
                DownloadAndParseFeedTask { podcast ->
                    podcast?.let {
                        cachedPodcast = it
                        PodcastCache().setCache(MyApplication.getApp(), it)
                        mCurrentPodcast.value = cachedPodcast
                    }
                }.execute(feed)
            }

            mCurrentPodcast.value = cachedPodcast
        }
        return mCurrentPodcast
    }
}
