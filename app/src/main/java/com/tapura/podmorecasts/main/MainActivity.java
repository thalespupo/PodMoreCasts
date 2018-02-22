package com.tapura.podmorecasts.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.details.PodcastDetailsActivity;
import com.tapura.podmorecasts.discover.DiscoverPodcastFragment;
import com.tapura.podmorecasts.favorite.FavoritePodcastFragment;

import java.util.function.DoubleUnaryOperator;

import static com.tapura.podmorecasts.discover.DiscoverPodcastFragment.FEED_URL_KEY;

public class MainActivity extends AppCompatActivity implements DiscoverPodcastFragment.PodcastClickListener {

    public static final String QUERY_KEY = "query";
    private FavoritePodcastFragment mFavoriteFragment;
    private DiscoverPodcastFragment mDiscoverFragment;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("thales", "onCreate: ");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            mFavoriteFragment = new FavoritePodcastFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment, mFavoriteFragment)
                    .commit();
        } else {
            mFavoriteFragment = (FavoritePodcastFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mDiscoverFragment = new DiscoverPodcastFragment();
                Bundle b = new Bundle();
                b.putString(QUERY_KEY, query);
                DiscoverPodcastFragment fragment = new DiscoverPodcastFragment();
                fragment.setArguments(b);
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.main_fragment, fragment)
                        .commit();

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        return true;
    }

    @Override
    public void onPodcastClick(String feed) {
        Intent intent = new Intent(this, PodcastDetailsActivity.class);
        intent.putExtra(FEED_URL_KEY, feed);
        startActivity(intent);
    }
}
