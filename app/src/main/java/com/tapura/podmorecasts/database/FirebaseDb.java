package com.tapura.podmorecasts.database;


import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapura.podmorecasts.model.Podcast;

import java.util.Objects;

public class FirebaseDb {

    private static final String USER_REF = "user";
    private static final String PODCASTS_LIST_REF = "podcasts";
    private static final String EPISODES_LIST_REF = "episodes";

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public boolean insertFavorite(Context context, Podcast podcast) {
        getPodcastFavoriteRef(context)
                .child(createHash(Objects.requireNonNull(podcast.getFeedUrl())))
                .setValue(podcast);
        return true;
    }

    public void removeFavorite(Context context, String feedUrl) {
        getPodcastFavoriteRef(context)
                .child(createHash(feedUrl))
                .removeValue();
    }

    public void attachPodcastListener(Context context, String feedUrl, ValueEventListener listener) {
        getPodcastFavoriteRef(context)
                .child(createHash(feedUrl))
                .addValueEventListener(listener);
    }

    public void detachPodcastListener(Context context, String feedUrl, ValueEventListener listener) {
        getPodcastFavoriteRef(context)
                .child(createHash(feedUrl))
                .removeEventListener(listener);
    }

    public void attachPodcastFavoriteListListener(Context context, ValueEventListener listener) {
        getPodcastFavoriteRef(context)
                .addValueEventListener(listener);
    }

    public void detachPodcastFavoriteListListener(Context context, ValueEventListener listener) {
        getPodcastFavoriteRef(context)
                .removeEventListener(listener);
    }

    public void getPodcast(Context context, String feed, ValueEventListener listener) {
        getPodcastFavoriteRef(context)
                .child(createHash(feed))
                .addListenerForSingleValueEvent(listener);
    }

    private DatabaseReference getPodcastFavoriteRef(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String userId = UserControlSharedPrefs.getAlreadyLoggedUserId(context);

        return database.getReference(USER_REF)
                .child(Objects.requireNonNull(userId))
                .child(PODCASTS_LIST_REF);
    }

    public void attachEpisodeListener(Context context, String feed, String guid, ValueEventListener listener) {
        getEpisodeListRef(context)
                .child(createHash(feed))
                .child(createHash(guid))
                .addValueEventListener(listener);
    }

    public void detachEpisodeListener(Context context, String feed, String guid, ValueEventListener listener) {
        getEpisodeListRef(context)
                .child(createHash(feed))
                .child(createHash(guid))
                .removeEventListener(listener);
    }

    private DatabaseReference getEpisodeListRef(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String userId = UserControlSharedPrefs.getAlreadyLoggedUserId(context);

        return database.getReference(USER_REF)
                .child(Objects.requireNonNull(userId))
                .child(EPISODES_LIST_REF);
    }

    private String createHash(String value) {
        return String.valueOf(value.hashCode());
    }
}
