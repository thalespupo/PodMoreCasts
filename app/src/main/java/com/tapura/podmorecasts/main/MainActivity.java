package com.tapura.podmorecasts.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.discover.DiscoverPodcastActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("thales", "onCreate: ");

        DiscoverPodcastActivity fragment = new DiscoverPodcastActivity();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_fragment, fragment)
                .commit();
    }
}
