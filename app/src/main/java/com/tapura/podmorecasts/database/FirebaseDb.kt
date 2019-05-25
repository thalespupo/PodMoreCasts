package com.tapura.podmorecasts.database


import android.content.Context
import com.google.firebase.database.*

import com.tapura.podmorecasts.MyApplication
import com.tapura.podmorecasts.model.Podcast

import java.util.Objects

class FirebaseDb {

    companion object {

        private const val USER_REF = "user"
        private const val PODCASTS_LIST_REF = "podcasts"
        private const val EPISODES_LIST_REF = "episodes"

        init {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
    }

    fun insertFavorite(context: Context, podcast: Podcast): Boolean {
        getPodcastFavoriteRef(context)
                .child(createHash(Objects.requireNonNull(podcast.feedUrl)))
                .setValue(podcast)
        return true
    }

    fun removeFavorite(context: Context, feedUrl: String) {
        getPodcastFavoriteRef(context)
                .child(createHash(feedUrl))
                .removeValue()
    }

    fun attachPodcastListener(context: Context, feedUrl: String, listener: ValueEventListener) {
        getPodcastFavoriteRef(context)
                .child(createHash(feedUrl))
                .addValueEventListener(listener)
    }

    fun detachPodcastListener(context: Context, feedUrl: String, listener: ValueEventListener) {
        getPodcastFavoriteRef(context)
                .child(createHash(feedUrl))
                .removeEventListener(listener)
    }

    fun attachPodcastFavoriteListListener(onSuccess: (ArrayList<Podcast>) -> Unit,
                                          onCancel: (String) -> Unit): ValueEventListener {

        val listener = object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val list = ArrayList<Podcast>()
                for (data in p0.children) {
                    data?.getValue(Podcast::class.java)?.let {
                        list.add(it)
                    }
                }
                onSuccess(list)
            }

            override fun onCancelled(p0: DatabaseError) {
                onCancel(p0.message)
            }
        }

        getPodcastFavoriteRef(MyApplication.getApp())
                .addValueEventListener(listener)

        return listener
    }

    fun detachPodcastFavoriteListListener(listener: ValueEventListener?) {
        listener?.let {
            getPodcastFavoriteRef(MyApplication.getApp())
                    .removeEventListener(it)
        }
    }

    fun getPodcast(context: Context, feed: String, listener: ValueEventListener) {
        getPodcastFavoriteRef(context)
                .child(createHash(feed))
                .addListenerForSingleValueEvent(listener)
    }

    private fun getPodcastFavoriteRef(context: Context): DatabaseReference {
        val database = FirebaseDatabase.getInstance()

        val userId = UserControlSharedPrefs.getAlreadyLoggedUserId(context)

        return database.getReference(USER_REF)
                .child(Objects.requireNonNull<String>(userId))
                .child(PODCASTS_LIST_REF)
    }

    fun attachEpisodeListener(context: Context, feed: String, guid: String, listener: ValueEventListener) {
        getEpisodeListRef(context)
                .child(createHash(feed))
                .child(createHash(guid))
                .addValueEventListener(listener)
    }

    fun detachEpisodeListener(context: Context, feed: String, guid: String, listener: ValueEventListener) {
        getEpisodeListRef(context)
                .child(createHash(feed))
                .child(createHash(guid))
                .removeEventListener(listener)
    }

    private fun getEpisodeListRef(context: Context): DatabaseReference {
        val database = FirebaseDatabase.getInstance()

        val userId = UserControlSharedPrefs.getAlreadyLoggedUserId(context)

        return database.getReference(USER_REF)
                .child(Objects.requireNonNull<String>(userId))
                .child(EPISODES_LIST_REF)
    }

    private fun createHash(value: String): String {
        return value.hashCode().toString()
    }
}
