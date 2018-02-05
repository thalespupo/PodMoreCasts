package com.tapura.podmorecasts.retrofit;


import com.tapura.podmorecasts.model.ItunesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ItunesSearchService {
    @GET("/search")
    public Call<ItunesResponse> getPodcasts(
            @Query("entity") String entity,
            @Query("term") String term);

}
