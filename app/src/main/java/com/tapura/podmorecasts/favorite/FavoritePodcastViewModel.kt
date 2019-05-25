package com.tapura.podmorecasts.favorite

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.database.ValueEventListener
import com.tapura.podmorecasts.MyLog
import com.tapura.podmorecasts.database.FirebaseDb
import com.tapura.podmorecasts.model.Podcast

class FavoritePodcastViewModel : ViewModel() {

    val loading by lazy { MutableLiveData<Boolean>() }
    val podcastList by lazy { MutableLiveData<ArrayList<Podcast>>() }
    val error by lazy { MutableLiveData<String>() }

    private val firebaseDb by lazy { FirebaseDb() }
    private var podcastListRef: ValueEventListener? = null

    override fun onCleared() {
        firebaseDb.detachPodcastFavoriteListListener(podcastListRef)
        super.onCleared()
    }

    fun loadPodcasts() {
        loading.postValue(true)

        podcastListRef = firebaseDb.attachPodcastFavoriteListListener({
            podcastList.postValue(it)
            loading.postValue(false)
        }, {
            MyLog.e(this.javaClass, it)
            error.postValue(it)
            loading.postValue(false)
        })
    }
}
