package com.tapura.podmorecasts.database;


import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapura.podmorecasts.model.Podcast;

import java.util.List;

public class FirebaseDb {

    private static final String TAG = FirebaseDb.class.getCanonicalName();

    private static String USER_REF = "user";

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public boolean insert(Podcast podcast, Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String uid = getUser(context);
        if (TextUtils.isEmpty(uid)) {
            return false;
        }

        String podcastId = String.valueOf(podcast.getFeedUrl().hashCode());
        DatabaseReference userRef = database.getReference(USER_REF).child(uid).child(podcastId);

        userRef.setValue(podcast);
        return true;
    }

    public void remove(Context context, String feedUrl) {

        String uid = getUser(context);
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String hashCode = String.valueOf(feedUrl.hashCode());

        DatabaseReference userRef = database.getReference(USER_REF).child(uid);

        userRef.child(hashCode).removeValue();
    }

    public void attachPodcastListener(Context context, String feedUrl, ValueEventListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String uid = getUser(context);
        if (TextUtils.isEmpty(uid)) {
            return;
        }

        int hashCode = feedUrl.hashCode();

        DatabaseReference podcastRef = database.getReference(USER_REF).child(uid).child(String.valueOf(hashCode));

        // Attach a listener to read the data at our posts reference
        podcastRef.addValueEventListener(listener);
    }

    public void detachPodcastListener(Context context, String feedUrl, ValueEventListener listener) {

        String uid = getUser(context);
        if (TextUtils.isEmpty(uid)) {
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        int hashCode = feedUrl.hashCode();

        DatabaseReference podcastRef = database.getReference(USER_REF).child(uid).child(String.valueOf(hashCode));

        // Detach the listener
        podcastRef.removeEventListener(listener);
    }

    public void attachPodcastListListener(Context context, ValueEventListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String uid = getUser(context);
        if (TextUtils.isEmpty(uid)) {
            return;
        }

        DatabaseReference podcastRef = database.getReference(USER_REF).child(uid);

        // Attach a listener to read the data at our posts reference
        podcastRef.addValueEventListener(listener);
    }

    public void detachPodcastListListener(Context context, ValueEventListener listener) {

        String uid = getUser(context);
        if (TextUtils.isEmpty(uid)) {
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference podcastRef = database.getReference(USER_REF).child(uid);

        // Detach the listener
        podcastRef.removeEventListener(listener);
    }

    public interface FirebasePodcastListListener {
        void onLoadedPodcastList(List<Podcast> podcasts);
    }

    public interface FirebasePodcastListener {
        void onLoadedPodcast(Podcast podcast);
    }

    private String getUser(Context context) {
        return UserControlSharedPrefs.getAlreadyLoggedUserId(context);
    }
}
