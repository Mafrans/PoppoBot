package me.mafrans.poppo.util.objects;

import lombok.Data;

import java.util.Date;

@Data
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
}
