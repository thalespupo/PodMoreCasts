package com.tapura.podmorecasts.discover;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.fake.FakeData;
import com.tapura.podmorecasts.model.ItunesResponse;
import com.tapura.podmorecasts.model.ItunesResultsItem;
import com.tapura.podmorecasts.retrofit.ItunesSearchService;
import com.tapura.podmorecasts.retrofit.ItunesSearchServiceBuilder;

import org.parceler.Parcels;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverPodcastActivity extends AppCompatActivity {

    private static final String TAG = DiscoverPodcastActivity.class.getCanonicalName();
    private static final String PODCAST_LIST_KEY = "podcast_list_key";

    private PodcastDiscoveredAdapter mAdapter;
    private RecyclerView mGridView;
    private SearchView mSearchView;
    private ItunesSearchService mSearchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_podcast);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGridView = findViewById(R.id.recycler_view_podcasts_discovered_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mAdapter = new PodcastDiscoveredAdapter(this);

        mGridView.setAdapter(mAdapter);
        mGridView.setLayoutManager(layoutManager);
        mGridView.setHasFixedSize(true);

        mSearchService = ItunesSearchServiceBuilder.build(this);

        mSearchView = findViewById(R.id.search_view);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (mSearchService != null) {
                    mSearchService.getPodcasts("podcast",query).enqueue(new Callback<ItunesResponse>() {
                        @Override
                        public void onResponse(Call<ItunesResponse> call, Response<ItunesResponse> response) {
                            if (response.isSuccessful()) {
                                ItunesResponse itunesResponse = response.body();
                                if (itunesResponse != null) {
                                    mAdapter.setPodcastList(itunesResponse.getResults());
                                }
                            } else {
                                Log.d(TAG, "onResponse is no successful: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ItunesResponse> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        if (savedInstanceState != null) {
            handleRotate(savedInstanceState);
        }
    }

    private void handleRotate(Bundle savedInstanceState) {
        List<ItunesResultsItem> list = Parcels.unwrap(savedInstanceState.getParcelable(PODCAST_LIST_KEY));
        mAdapter.setPodcastList(list);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(PODCAST_LIST_KEY, Parcels.wrap(mAdapter.getList()));
        super.onSaveInstanceState(outState);
    }
}
