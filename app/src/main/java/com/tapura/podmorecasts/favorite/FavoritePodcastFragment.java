package com.tapura.podmorecasts.favorite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Podcast;

import java.util.ArrayList;
import java.util.List;

public class FavoritePodcastFragment extends Fragment implements PodcastFavoritedAdapter.PodcastFavoritedOnClickListener, ValueEventListener {

    private static final String TAG = FavoritePodcastFragment.class.getCanonicalName();
    public static String fragTag = "favorite";

    private PodcastFavoritedAdapter mAdapter;
    private RecyclerView mGridView;
    private ProgressBar progressBar;
    private FirebaseDb firebaseDb;

    public FavoritePodcastFragment() {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<Podcast> list = new ArrayList<>();
        for (DataSnapshot data :dataSnapshot.getChildren()) {
            list.add(data.getValue(Podcast.class));
        }
        mAdapter.setPodcastList(list);
        stopLoadingScheme();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(getActivity(), "List not loaded", Toast.LENGTH_SHORT).show();
    }

    public interface FavoriteClickListener {
        void onFavoritePodcastClick(String feed);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.content_favorite_podcast, container, false);

        mGridView = view.findViewById(R.id.recycler_view_podcasts_favorited_list);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

        mAdapter = new PodcastFavoritedAdapter(getContext(), this);

        mGridView.setAdapter(mAdapter);
        mGridView.setLayoutManager(layoutManager);
        mGridView.setHasFixedSize(true);

        firebaseDb = new FirebaseDb();

        firebaseDb.attachPodcastListListener(getActivity(), this);
        startLoadingScheme();
        return view;
    }

    @Override
    public void onClick(int pos) {
        String feedUrl = mAdapter.getList().get(pos).getFeedUrl();
        if (getActivity() != null) {
            ((FavoriteClickListener) getActivity()).onFavoritePodcastClick(feedUrl);
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
