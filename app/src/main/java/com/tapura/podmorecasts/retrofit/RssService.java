package com.tapura.podmorecasts.retrofit;


import retrofit2.Call;
import retrofit2.http.GET;

public interface RssService {
    @GET("")
    public Call<String> getPodcasts();
}
