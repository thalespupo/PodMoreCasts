package com.tapura.podmorecasts.database;


import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapura.podmorecasts.model.Podcast;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDb {

    private static String USER_REF = "user";

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    private static List<Podcast> list;

    public static boolean insert(Podcast podcast, Context context) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        if (currentUser == null) {
            uid = UserControlSharedPrefs.getAlreadyLoggedUserId(context);
        } else {
            uid = currentUser.getUid();
        }

        if (uid == null) {
            return false;
        }

        String podcastId = String.valueOf(podcast.getFeedUrl().hashCode());
        DatabaseReference userRef = database.getReference(USER_REF).child(uid).child(podcastId);

        userRef.setValue(podcast);
        return true;
    }

    public static boolean getList(Context context, final PodcastListFromFirebaseListener callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        if (currentUser == null) {
            uid = UserControlSharedPrefs.getAlreadyLoggedUserId(context);
        } else {
            uid = currentUser.getUid();
        }

        if (uid == null) {
            return false;
        }

        DatabaseReference userRef = database.getReference(USER_REF).child(uid);

        // Attach a listener to read the data at our posts reference
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Podcast> list = new ArrayList<>();
                for (DataSnapshot data :dataSnapshot.getChildren()) {
                    list.add(data.getValue(Podcast.class));
                }
                callback.onLoadedPodcastList(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        return true;
    }

    public static boolean getPodcast(Context context, String feedUrl, final PodcastFromFirebaseListener callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        if (currentUser == null) {
            uid = UserControlSharedPrefs.getAlreadyLoggedUserId(context);
        } else {
            uid = currentUser.getUid();
        }

        if (uid == null) {
            return false;
        }

        int hashCode = feedUrl.hashCode();

        DatabaseReference userRef = database.getReference(USER_REF).child(uid).child(String.valueOf(hashCode));

        // Attach a listener to read the data at our posts reference
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onLoadedPodcast(dataSnapshot.getValue(Podcast.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        return true;
    }


    public interface PodcastListFromFirebaseListener {
        void onLoadedPodcastList(List<Podcast> podcasts);
    }

    public interface PodcastFromFirebaseListener {
        void onLoadedPodcast(Podcast podcast);
    }
}
