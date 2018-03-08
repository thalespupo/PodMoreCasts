package com.tapura.podmorecasts.download;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadRequestRepository {

    private static final String SHARED_PREF_KEY = "download_request_repo";
    private final Context mContext;
    private List<DownloadRequest> mRequests;

    public DownloadRequestRepository(Context c) {
        mContext = c;
        mRequests = getRequestsFromSharedPreferences();
    }

    private List<DownloadRequest> getRequestsFromSharedPreferences() {
        List<DownloadRequest> list = new ArrayList<>();
        SharedPreferences sharedPref = mContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Map<String, ?> all = sharedPref.getAll();
        for (String index : all.keySet()) {
            String object = (String) all.get(index);
            list.add(gson.fromJson(object, DownloadRequest.class));
        }
        return list;
    }

    @Nullable
    public DownloadRequest get(long refId) {
        for (DownloadRequest r : mRequests) {
            if (r.getRefId() == refId) {
                return r;
            }
        }
        return null;
    }

    public void append(DownloadRequest downloadRequest) {
        mRequests.add(downloadRequest);
        flush();
    }

    public long getId(String feed, int epiIndex) {
        for (DownloadRequest r : mRequests) {
            if (feed.equals(r.getFeed()) && epiIndex == r.getEpisodePos()) {
                return r.getRefId();
            }
        }
        return -1;
    }

    public void remove(long id) {
        DownloadRequest chosen = null;
        for (DownloadRequest r : mRequests) {
            if (r.getRefId() == id) {
                chosen = r;
                break;
            }
        }
        mRequests.remove(chosen);
        flush();
    }

    private void flush() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        for (DownloadRequest request : mRequests) {
            String stringRequest = gson.toJson(request);
            editor.putString(String.valueOf(request.getRefId()), stringRequest);
        }

        editor.apply();
    }
}
