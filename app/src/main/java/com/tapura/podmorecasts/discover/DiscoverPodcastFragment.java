package com.tapura.podmorecasts.discover;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.model.ItunesResultsItem;

import java.util.List;

import static com.tapura.podmorecasts.main.MainActivity.QUERY_KEY;

public class DiscoverPodcastFragment extends Fragment implements PodcastDiscoverAdapter.PodcastDiscoverOnClickListener {

    public static String fragTag = "discover";

    private PodcastDiscoverAdapter mAdapter;
    private RecyclerView mGridView;
    private ProgressBar progressBar;
    private PodcastDiscoverViewModel mModel;
    private Observer<List<ItunesResultsItem>> mObserver;

    public DiscoverPodcastFragment() {

    }

    public interface PodcastClickListener {
        void onPodcastClick(String feed, String thumbnail);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MyLog.d(getClass(), "onCreateView: ");
        View view = inflater.inflate(R.layout.content_discover_podcast, container, false);

        mGridView = view.findViewById(R.id.recycler_view_podcasts_discover_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mAdapter = new PodcastDiscoverAdapter(getContext(), this);

        mGridView.setAdapter(mAdapter);
        mGridView.setLayoutManager(layoutManager);
        mGridView.setHasFixedSize(true);

        createObserver();
        createViewModel();

        if (getArguments() != null) {
            String query = getArguments().getString(QUERY_KEY);
            if (!TextUtils.isEmpty(query)) {
                searchFeedByQuery(getArguments().getString(QUERY_KEY));
            }
        }

        return view;
    }

    private void createObserver() {
        mObserver = new Observer<List<ItunesResultsItem>>() {

            @Override
            public void onChanged(@Nullable List<ItunesResultsItem> itunesResultsItems) {
                if (itunesResultsItems != null && !itunesResultsItems.isEmpty()) {
                    mAdapter.setPodcastList(itunesResultsItems);
                    stopLoadingScheme();
                }
            }
        };
    }

    private void createViewModel() {
        mModel = ViewModelProviders.of(this).get(PodcastDiscoverViewModel.class);
        mModel.getCurrentList().observe(this, mObserver);
    }

    public void searchFeedByQuery(String query) {
        startLoadingScheme();
        mModel.getCurrentList(query).observe(this, mObserver);

    }

    @Override
    public void onClick(int pos) {
        String feedUrl = mAdapter.getList().get(pos).getFeedUrl();
        String thumbnail = mAdapter.getList().get(pos).getArtworkUrl600();
        if (getActivity() != null) {
            ((PodcastClickListener) getActivity()).onPodcastClick(feedUrl, thumbnail);
        }
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
