package com.tapura.podmorecasts.details;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Podcast;

public class PodcastDetailsActivity extends AppCompatActivity {

    private Podcast mPodcast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_podcast);

        // TODO Podcast is null
        mPodcast = null;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mPodcast.getTitle());

        ImageView ivThumbnail = findViewById(R.id.thumbnail);

        Picasso.with(this)
                .load(mPodcast.getImagePath())
                .into(ivThumbnail);

        TextView tvAuthor = findViewById(R.id.text_view_podcast_author);
        TextView tvSummary = findViewById(R.id.text_view_podcast_summary);

        tvAuthor.setText(mPodcast.getAuthor());
        tvSummary.setText(mPodcast.getSummary());


    }

    public void favoritePodcast(View view) {
        FirebaseDb.insert(mPodcast);
        Toast.makeText(this, "The Podcast " + mPodcast.getTitle() + " was added!", Toast.LENGTH_LONG).show();
    }
}
