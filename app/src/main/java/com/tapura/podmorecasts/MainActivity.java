package com.tapura.podmorecasts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.tapura.podmorecasts.database.UserControlSharedPrefs;
import com.tapura.podmorecasts.details.PodcastDetailsActivity;
import com.tapura.podmorecasts.discover.DiscoverPodcastActivity;
import com.tapura.podmorecasts.player.PlayerTestActivity;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.EmailBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*Intent intent = new Intent(this, PodcastDetailsActivity.class);
        startActivity(intent);
        finish();*/

         /*
        Intent intent = new Intent(this, DiscoverPodcastActivity.class);
        startActivity(intent);
        finish();
        */

        String loggedUserId = UserControlSharedPrefs.getAlreadyLoggedUserId(this);

        if (TextUtils.isEmpty(loggedUserId)) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        } else {
            startPodMoreCastsEntryPoint();
        }
    }

    private void startPodMoreCastsEntryPoint() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        String userId = null;
        if (user != null) {
            userId = user.getUid();
        }
        if (TextUtils.isEmpty(userId)) {
            throw new SecurityException("No user ID found in Firebase database OR null User");
        } else {
            UserControlSharedPrefs.setUserId(this, userId);
            Intent intent = new Intent(this, DiscoverPodcastActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                startPodMoreCastsEntryPoint();
                // ...
            } else {
                finish();
                // Sign in failed, check response for error code
                // ...
            }
        }
    }
}
