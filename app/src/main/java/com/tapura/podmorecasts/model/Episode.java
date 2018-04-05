package com.tapura.podmorecasts.model;

public class Episode {

    private String title;
    private String link;
    private String episodeLink;
    private String description;
    private String pathInDisk;
    private EpisodeMediaState episodeState;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getEpisodeLink() {
        return episodeLink;
    }

    public void setEpisodeLink(String episodeLink) {
        this.episodeLink = episodeLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPathInDisk() {
        return pathInDisk;
    }

    public void setPathInDisk(String pathInDisk) {
        this.pathInDisk = pathInDisk;
    }

    public EpisodeMediaState getEpisodeState() {
        return episodeState;
    }

    public void setEpisodeState(EpisodeMediaState episodeState) {
        this.episodeState = episodeState;
    }
}
