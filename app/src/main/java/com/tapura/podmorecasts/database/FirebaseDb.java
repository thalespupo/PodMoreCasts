package com.tapura.podmorecasts.database;


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

    public static void insert(Podcast podcast) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String uid = currentUser.getUid();

        String podcastId = String.valueOf(podcast.getFeedUrl().hashCode());
        DatabaseReference userRef = database.getReference(USER_REF).child(uid).child(podcastId);

        userRef.setValue(podcast);
        
    }
}
