package me.mafrans.poppo.util.objects;

import java.util.Date;

public class YoutubeVideo {
    private String videoId;
    private String title;
    private Date published;
    private String channelId;
    private String channelTitle;
    private String description;
    private String[] images;

    public YoutubeVideo(String videoId, String title, Date published, String channelId, String channelTitle, String description, String[] images) {
        this.videoId = videoId;
        this.title = title;
        this.published = published;
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.description = description;
        this.images = images;
    }

    public YoutubeVideo() {
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoUrl() {
        return "https://www.youtube.com/watch?v=" + videoId;
    }

    public String getImageUrl() {
        return images[0];
    }

    public String getMediumImageUrl() {
        return images[1];
    }

    public String getHighImageUrl() {
        return images[3];
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }
}
