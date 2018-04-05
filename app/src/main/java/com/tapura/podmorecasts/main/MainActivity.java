package com.tapura.podmorecasts.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.details.PodcastDetailsActivity;
import com.tapura.podmorecasts.discover.DiscoverPodcastFragment;
import com.tapura.podmorecasts.favorite.FavoritePodcastFragment;

public class MainActivity extends AppCompatActivity implements DiscoverPodcastFragment.PodcastClickListener, FavoritePodcastFragment.FavoriteClickListener {

    public static final String QUERY_KEY = "query";
    public static final String THUMBNAIL_KEY = "thumbnail";
    public static final String FEED_URL_KEY = "feed_url";
    private static final String FAVORITE_KEY = "favorite";

    private DiscoverPodcastFragment mDiscoverFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyLog.d(getClass(), "onCreate: ");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FavoritePodcastFragment mFavoriteFragment;
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

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

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
                if (mDiscoverFragment != null && mDiscoverFragment.isVisible()) {
                    getSupportFragmentManager().popBackStack();
                }
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
        Intent intent = new Intent(this, PodcastDetailsActivity.class);
        intent.putExtra(FEED_URL_KEY, feed);
        intent.putExtra(FAVORITE_KEY, true);
        startActivity(intent);
    }

}
