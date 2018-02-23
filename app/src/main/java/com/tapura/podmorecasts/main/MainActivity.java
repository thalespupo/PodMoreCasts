package com.tapura.podmorecasts.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.details.PodcastDetailsActivity;
import com.tapura.podmorecasts.discover.DiscoverPodcastFragment;
import com.tapura.podmorecasts.favorite.FavoritePodcastFragment;

public class MainActivity extends AppCompatActivity implements DiscoverPodcastFragment.PodcastClickListener, FavoritePodcastFragment.FavoriteClickListener {

    private static final String TAG = MainActivity.class.getCanonicalName();

    public static final String QUERY_KEY = "query";
    public static final String THUMBNAIL_KEY = "thumbnail";
    public static final String FEED_URL_KEY = "feed_url";

    private FavoritePodcastFragment mFavoriteFragment;
    private DiscoverPodcastFragment mDiscoverFragment;

    private MenuItem searchItem;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            mFavoriteFragment = new FavoritePodcastFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment, mFavoriteFragment, FavoritePodcastFragment.fragTag)
                    .commit();
        } else {
            mDiscoverFragment = (DiscoverPodcastFragment) getSupportFragmentManager().findFragmentByTag(DiscoverPodcastFragment.fragTag);
            mFavoriteFragment = (FavoritePodcastFragment) getSupportFragmentManager().findFragmentByTag(FavoritePodcastFragment.fragTag);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mDiscoverFragment = new DiscoverPodcastFragment();
                Bundle b = new Bundle();
                b.putString(QUERY_KEY, query);

                mDiscoverFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.main_fragment, mDiscoverFragment, DiscoverPodcastFragment.fragTag)
                        .commit();

                getCurrentFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                return true;
            }
        });

        return true;
    }

    @Override
    public void onPodcastClick(String feed, String thumbnail) {
        Intent intent = new Intent(this, PodcastDetailsActivity.class);
        intent.putExtra(FEED_URL_KEY, feed);
        intent.putExtra(THUMBNAIL_KEY, thumbnail);
        startActivity(intent);
    }

    @Override
    public void onFavoritePodcastClick(String feed) {
        // TODO retreive information in PodcastDetailsActivity
        /*
        Intent intent = new Intent(this, PodcastDetailsActivity.class);
        intent.putExtra(FEED_URL_KEY, feed);
        startActivity(intent);
        */
    }

}
