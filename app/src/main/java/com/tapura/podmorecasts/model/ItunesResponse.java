package com.tapura.podmorecasts.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItunesResponse {

    @SerializedName("resultCount")
    private int resultCount;

    @SerializedName("results")
    private List<ItunesResultsItem> results;

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResults(List<ItunesResultsItem> results) {
        this.results = results;
    }

    public List<ItunesResultsItem> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return
                "ItunesResponse{" +
                        "resultCount = '" + resultCount + '\'' +
                        ",results = '" + results + '\'' +
                        "}";
    }
}