package me.mafrans.poppo.util.objects;

import java.util.Date;

public class Anime {
    private int malId;
    private String url;
    private String imageUrl;
    private boolean airing;
    private String synopsis;
    private String type;
    private int episodes;
    private double score;
    private Date startDate;
    private Date endDate;
    private int members;
    private String rating;
    private String title;

    public Anime(int malId, String url, String imageUrl, boolean airing, String synopsis, String type, int episodes, double score, Date startDate, Date endDate, int members, String rating, String title) {
        this.malId = malId;
        this.url = url;
        this.imageUrl = imageUrl;
        this.airing = airing;
        this.synopsis = synopsis;
        this.type = type;
        this.episodes = episodes;
        this.score = score;
        this.startDate = startDate;
        this.endDate = endDate;
        this.members = members;
        this.rating = rating;
        this.title = title;
    }

    public Anime() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMalId() {
        return malId;
    }

    public void setMalId(int malId) {
        this.malId = malId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAiring() {
        return airing;
    }

    public void setAiring(boolean airing) {
        this.airing = airing;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
