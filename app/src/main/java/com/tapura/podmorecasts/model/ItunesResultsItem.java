package com.tapura.podmorecasts.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class ItunesResultsItem {

    @SerializedName("artworkUrl100")
    private String artworkUrl100;

    @SerializedName("trackHdRentalPrice")
    private int trackHdRentalPrice;

    @SerializedName("country")
    private String country;

    @SerializedName("collectionHdPrice")
    private int collectionHdPrice;

    @SerializedName("trackName")
    private String trackName;

    @SerializedName("artworkUrl600")
    private String artworkUrl600;

    @SerializedName("collectionName")
    private String collectionName;

    @SerializedName("trackCount")
    private int trackCount;

    @SerializedName("genres")
    private List<String> genres;

    @SerializedName("artworkUrl30")
    private String artworkUrl30;

    @SerializedName("wrapperType")
    private String wrapperType;

    @SerializedName("currency")
    private String currency;

    @SerializedName("collectionId")
    private int collectionId;

    @SerializedName("trackExplicitness")
    private String trackExplicitness;

    @SerializedName("feedUrl")
    private String feedUrl;

    @SerializedName("collectionViewUrl")
    private String collectionViewUrl;

    @SerializedName("trackHdPrice")
    private int trackHdPrice;

    @SerializedName("contentAdvisoryRating")
    private String contentAdvisoryRating;

    @SerializedName("releaseDate")
    private String releaseDate;

    @SerializedName("kind")
    private String kind;

    @SerializedName("trackId")
    private int trackId;

    @SerializedName("trackRentalPrice")
    private int trackRentalPrice;

    @SerializedName("collectionPrice")
    private double collectionPrice;

    @SerializedName("genreIds")
    private List<String> genreIds;

    @SerializedName("primaryGenreName")
    private String primaryGenreName;

    @SerializedName("trackPrice")
    private double trackPrice;

    @SerializedName("collectionExplicitness")
    private String collectionExplicitness;

    @SerializedName("trackViewUrl")
    private String trackViewUrl;

    @SerializedName("artworkUrl60")
    private String artworkUrl60;

    @SerializedName("trackCensoredName")
    private String trackCensoredName;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("collectionCensoredName")
    private String collectionCensoredName;

    public void setArtworkUrl100(String artworkUrl100) {
        this.artworkUrl100 = artworkUrl100;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public void setTrackHdRentalPrice(int trackHdRentalPrice) {
        this.trackHdRentalPrice = trackHdRentalPrice;
    }

    public int getTrackHdRentalPrice() {
        return trackHdRentalPrice;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCollectionHdPrice(int collectionHdPrice) {
        this.collectionHdPrice = collectionHdPrice;
    }

    public int getCollectionHdPrice() {
        return collectionHdPrice;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setArtworkUrl600(String artworkUrl600) {
        this.artworkUrl600 = artworkUrl600;
    }

    public String getArtworkUrl600() {
        return artworkUrl600;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setArtworkUrl30(String artworkUrl30) {
        this.artworkUrl30 = artworkUrl30;
    }

    public String getArtworkUrl30() {
        return artworkUrl30;
    }

    public void setWrapperType(String wrapperType) {
        this.wrapperType = wrapperType;
    }

    public String getWrapperType() {
        return wrapperType;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setTrackExplicitness(String trackExplicitness) {
        this.trackExplicitness = trackExplicitness;
    }

    public String getTrackExplicitness() {
        return trackExplicitness;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setCollectionViewUrl(String collectionViewUrl) {
        this.collectionViewUrl = collectionViewUrl;
    }

    public String getCollectionViewUrl() {
        return collectionViewUrl;
    }

    public void setTrackHdPrice(int trackHdPrice) {
        this.trackHdPrice = trackHdPrice;
    }

    public int getTrackHdPrice() {
        return trackHdPrice;
    }

    public void setContentAdvisoryRating(String contentAdvisoryRating) {
        this.contentAdvisoryRating = contentAdvisoryRating;
    }

    public String getContentAdvisoryRating() {
        return contentAdvisoryRating;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getKind() {
        return kind;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackRentalPrice(int trackRentalPrice) {
        this.trackRentalPrice = trackRentalPrice;
    }

    public int getTrackRentalPrice() {
        return trackRentalPrice;
    }

    public void setCollectionPrice(double collectionPrice) {
        this.collectionPrice = collectionPrice;
    }

    public double getCollectionPrice() {
        return collectionPrice;
    }

    public void setGenreIds(List<String> genreIds) {
        this.genreIds = genreIds;
    }

    public List<String> getGenreIds() {
        return genreIds;
    }

    public void setPrimaryGenreName(String primaryGenreName) {
        this.primaryGenreName = primaryGenreName;
    }

    public String getPrimaryGenreName() {
        return primaryGenreName;
    }

    public void setTrackPrice(double trackPrice) {
        this.trackPrice = trackPrice;
    }

    public double getTrackPrice() {
        return trackPrice;
    }

    public void setCollectionExplicitness(String collectionExplicitness) {
        this.collectionExplicitness = collectionExplicitness;
    }

    public String getCollectionExplicitness() {
        return collectionExplicitness;
    }

    public void setTrackViewUrl(String trackViewUrl) {
        this.trackViewUrl = trackViewUrl;
    }

    public String getTrackViewUrl() {
        return trackViewUrl;
    }

    public void setArtworkUrl60(String artworkUrl60) {
        this.artworkUrl60 = artworkUrl60;
    }

    public String getArtworkUrl60() {
        return artworkUrl60;
    }

    public void setTrackCensoredName(String trackCensoredName) {
        this.trackCensoredName = trackCensoredName;
    }

    public String getTrackCensoredName() {
        return trackCensoredName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setCollectionCensoredName(String collectionCensoredName) {
        this.collectionCensoredName = collectionCensoredName;
    }

    public String getCollectionCensoredName() {
        return collectionCensoredName;
    }

    @Override
    public String toString() {
        return
                "ItunesResultsItem{" +
                        "artworkUrl100 = '" + artworkUrl100 + '\'' +
                        ",trackHdRentalPrice = '" + trackHdRentalPrice + '\'' +
                        ",country = '" + country + '\'' +
                        ",collectionHdPrice = '" + collectionHdPrice + '\'' +
                        ",trackName = '" + trackName + '\'' +
                        ",artworkUrl600 = '" + artworkUrl600 + '\'' +
                        ",collectionName = '" + collectionName + '\'' +
                        ",trackCount = '" + trackCount + '\'' +
                        ",genres = '" + genres + '\'' +
                        ",artworkUrl30 = '" + artworkUrl30 + '\'' +
                        ",wrapperType = '" + wrapperType + '\'' +
                        ",currency = '" + currency + '\'' +
                        ",collectionId = '" + collectionId + '\'' +
                        ",trackExplicitness = '" + trackExplicitness + '\'' +
                        ",feedUrl = '" + feedUrl + '\'' +
                        ",collectionViewUrl = '" + collectionViewUrl + '\'' +
                        ",trackHdPrice = '" + trackHdPrice + '\'' +
                        ",contentAdvisoryRating = '" + contentAdvisoryRating + '\'' +
                        ",releaseDate = '" + releaseDate + '\'' +
                        ",kind = '" + kind + '\'' +
                        ",trackId = '" + trackId + '\'' +
                        ",trackRentalPrice = '" + trackRentalPrice + '\'' +
                        ",collectionPrice = '" + collectionPrice + '\'' +
                        ",genreIds = '" + genreIds + '\'' +
                        ",primaryGenreName = '" + primaryGenreName + '\'' +
                        ",trackPrice = '" + trackPrice + '\'' +
                        ",collectionExplicitness = '" + collectionExplicitness + '\'' +
                        ",trackViewUrl = '" + trackViewUrl + '\'' +
                        ",artworkUrl60 = '" + artworkUrl60 + '\'' +
                        ",trackCensoredName = '" + trackCensoredName + '\'' +
                        ",artistName = '" + artistName + '\'' +
                        ",collectionCensoredName = '" + collectionCensoredName + '\'' +
                        "}";
    }
}