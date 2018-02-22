package com.tapura.podmorecasts.favorite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Podcast;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritePodcastFragment extends Fragment implements PodcastFavoritedAdapter.PodcastFavoritedOnClickListener, FirebaseDb.PodcastFromFirebaseListener {

    private static final String TAG = FavoritePodcastFragment.class.getCanonicalName();
    private static final String PODCAST_LIST_KEY = "podcast_list_key";
    public static final String FEED_URL_KEY = "feed_url";

    private PodcastFavoritedAdapter mAdapter;
    private RecyclerView mGridView;
    private ProgressBar progressBar;

    public FavoritePodcastFragment() {

    }

    @Override
    public void onLoadedPodcasts(List<Podcast> podcasts) {
        mAdapter.setPodcastList(podcasts);
        stopLoadingScheme();
    }

    public interface PodcastClickListener {
        void onPodcastClick(String feed);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("thales", "onCreateView: ");
        View view = inflater.inflate(R.layout.content_favorite_podcast, container, false);

        mGridView = view.findViewById(R.id.recycler_view_podcasts_favorited_list);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

        mAdapter = new PodcastFavoritedAdapter(getContext(), this);

        mGridView.setAdapter(mAdapter);
        mGridView.setLayoutManager(layoutManager);
        mGridView.setHasFixedSize(true);

        FirebaseDb.getList(getContext(), this);
        startLoadingScheme();
        return view;
    }

    @Override
    public void onClick(int pos) {
        Toast.makeText(getContext(), "Not implemented", Toast.LENGTH_SHORT).show();
        /*
        String feedUrl = mAdapter.getList().get(pos).getFeedUrl();
        if (getActivity() != null) {
            ((PodcastClickListener) getActivity()).onPodcastClick(feedUrl);
        }
        */
    }

    private void startLoadingScheme() {
        if (getActivity() != null) {
            progressBar = getActivity().findViewById(R.id.layout_loading_progressbar);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void stopLoadingScheme() {
        if (getActivity() != null) {
            progressBar = getActivity().findViewById(R.id.layout_loading_progressbar);
            progressBar.setVisibility(View.GONE);
        }
    }
}
