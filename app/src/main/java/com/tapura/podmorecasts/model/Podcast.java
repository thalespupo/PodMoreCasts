package com.tapura.podmorecasts.model;


import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Podcast {

    private String title;

    private String author;

    private String summary;

    private String imagePath;

    private String thumbnailPath;

    private List<Episode> episodes;

    private String feedUrl;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getSummary() {
        return summary;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

}
