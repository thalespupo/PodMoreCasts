package com.tapura.podmorecasts.database;


import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tapura.podmorecasts.model.Podcast;

public class FirebaseDb {

    private static String USER_REF = "user";

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

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
}
