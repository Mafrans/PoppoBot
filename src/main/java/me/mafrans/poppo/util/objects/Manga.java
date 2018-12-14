package me.mafrans.poppo.util.objects;

import java.util.Date;

public class Manga {
    private int malId;
    private String url;
    private String imageUrl;
    private boolean published;
    private String synopsis;
    private String type;
    private int chapters;
    private int volumes;
    private double score;
    private Date startDate;
    private Date endDate;
    private int members;
    private String title;

    public Manga() {
    }

    public Manga(int malId, String url, String imageUrl, boolean published, String synopsis, String type, int chapters, int volumes, double score, Date startDate, Date endDate, int members, String title) {
        this.malId = malId;
        this.url = url;
        this.imageUrl = imageUrl;
        this.published = published;
        this.synopsis = synopsis;
        this.type = type;
        this.chapters = chapters;
        this.volumes = volumes;
        this.score = score;
        this.startDate = startDate;
        this.endDate = endDate;
        this.members = members;
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

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
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

    public int getChapters() {
        return chapters;
    }

    public void setChapters(int chapters) {
        this.chapters = chapters;
    }

    public int getVolumes() {
        return volumes;
    }

    public void setVolumes(int volumes) {
        this.volumes = volumes;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
