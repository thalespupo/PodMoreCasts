package com.tapura.podmorecasts.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.details.PodcastDetailsActivity;
import com.tapura.podmorecasts.model.ItunesResponse;
import com.tapura.podmorecasts.model.ItunesResultsItem;
import com.tapura.podmorecasts.retrofit.ItunesSearchService;
import com.tapura.podmorecasts.retrofit.ItunesSearchServiceBuilder;

import org.parceler.Parcels;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverPodcastActivity extends Fragment implements PodcastDiscoveredAdapter.PodcastDiscoveredOnClickListener {

    private static final String TAG = DiscoverPodcastActivity.class.getCanonicalName();
    private static final String PODCAST_LIST_KEY = "podcast_list_key";
    public static final String FEED_URL_KEY = "feed_url";

    private PodcastDiscoveredAdapter mAdapter;
    private RecyclerView mGridView;
    private ItunesSearchService mSearchService;

    public DiscoverPodcastActivity () {

    }

    public interface PodcastClickListener {
        void onPodcastClick(String feed);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("thales", "onCreateView: ");
        View view = inflater.inflate(R.layout.content_discover_podcast, container, false);

        mGridView = view.findViewById(R.id.recycler_view_podcasts_discovered_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mAdapter = new PodcastDiscoveredAdapter(getContext(), this);

        mGridView.setAdapter(mAdapter);
        mGridView.setLayoutManager(layoutManager);
        mGridView.setHasFixedSize(true);

        mSearchService = ItunesSearchServiceBuilder.build(getContext());

        if (savedInstanceState != null) {
            handleRotate(savedInstanceState);
        }

        return view;
    }

    public void searchFeedByQuery(String query) {
        if (mSearchService != null) {
            mSearchService.getPodcasts("podcast", query).enqueue(new Callback<ItunesResponse>() {
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

    @Override
    public void onClick(int pos) {
        String feedUrl = mAdapter.getList().get(pos).getFeedUrl();
        if (getActivity() != null) {
            ((PodcastClickListener)getActivity()).onPodcastClick(feedUrl);
        }
    }
}
