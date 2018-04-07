package com.tapura.podmorecasts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tapura.podmorecasts.database.UserControlSharedPrefs;
import com.tapura.podmorecasts.main.MainActivity;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private final List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.EmailBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        String userId = null;
        if (user != null) {
            userId = user.getUid();
        }
        if (TextUtils.isEmpty(userId)) {
            throw new SecurityException("No user ID found in Firebase database OR null User");
        } else {
            UserControlSharedPrefs.setUserId(this, userId);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                startPodMoreCastsEntryPoint();
                // ...
            } else {
                Toast.makeText(this, getString(R.string.toast_firebase_login_error), Toast.LENGTH_SHORT).show();
                finish();
                // Sign in failed, check response for error code
                // ...
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_firebase_login_error), Toast.LENGTH_SHORT).show();
        }
    }
}
