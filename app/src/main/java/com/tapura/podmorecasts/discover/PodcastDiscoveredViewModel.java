package com.tapura.podmorecasts.discover;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tapura.podmorecasts.MyApplication;
import com.tapura.podmorecasts.model.ItunesResponse;
import com.tapura.podmorecasts.model.ItunesResultsItem;
import com.tapura.podmorecasts.retrofit.ItunesSearchService;
import com.tapura.podmorecasts.retrofit.ItunesSearchServiceBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PodcastDiscoveredViewModel extends ViewModel {

    private static final String TAG = PodcastDiscoveredViewModel.class.getCanonicalName();

    private MutableLiveData<List<ItunesResultsItem>> mCurrentList;
    private ItunesSearchService mSearchService;

    public MutableLiveData<List<ItunesResultsItem>> getCurrentList() {
        if (mCurrentList == null) {
            mCurrentList = new MutableLiveData<>();
        }
        return mCurrentList;
    }

    public MutableLiveData<List<ItunesResultsItem>> getCurrentList(String query) {
        if (mSearchService == null) {
            mSearchService = ItunesSearchServiceBuilder.build(MyApplication.getApp());
        }

        mSearchService.getPodcasts("podcast", query).enqueue(new Callback<ItunesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ItunesResponse> call, @NonNull Response<ItunesResponse> response) {
                if (response.isSuccessful()) {
                    ItunesResponse itunesResponse = response.body();
                    if (itunesResponse != null) {
                        mCurrentList.setValue(itunesResponse.getResults());
                    }
                } else {
                    Log.d(TAG, "onResponse is no successful: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ItunesResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return mCurrentList;
    }
}
