package com.tapura.podmorecasts.details


import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.tapura.podmorecasts.MyApplication
import com.tapura.podmorecasts.cache.PodcastCache
import com.tapura.podmorecasts.database.FirebaseDb
import com.tapura.podmorecasts.model.Podcast

class PodcastDetailsViewModel : ViewModel() {

    private lateinit var mCurrentPodcast: MutableLiveData<Podcast>

    private val cache = PodcastCache()

    fun getCurrentPodcast(feed: String): MutableLiveData<Podcast> {
        if (::mCurrentPodcast.isInitialized.not()) {
            mCurrentPodcast = MutableLiveData()

            val cachedPodcast = cache.getCache(MyApplication.getApp(), feed)

            if (cachedPodcast == null) {
                Log.d("thales", "cache null")
                DownloadAndParseFeedTask { podcast ->
                    podcast?.let { discoverIfItIsFav(it) }
                }.execute(feed)
            }

            mCurrentPodcast.value = cachedPodcast
        }
        return mCurrentPodcast
    }

    private fun discoverIfItIsFav(podcast: Podcast) {
        FirebaseDb().getPodcast(MyApplication.getApp(),
                podcast.feedUrl,
                object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("thales", "getPodcast cancelled, nothing to do, ret=$error")
                    }

                    override fun onDataChange(data: DataSnapshot) {
                        podcast.isFavorite = data.exists()
                        cache.setCache(MyApplication.getApp(), podcast)
                        mCurrentPodcast.value = podcast
                    }
                })
    }

    fun invalidateCache() {
        mCurrentPodcast.value?.let {
            it.isFavorite = false
            mCurrentPodcast.value = it

            cache.setCache(MyApplication.getApp(), it)
        }
    }
}
