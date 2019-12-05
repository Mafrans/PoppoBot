package me.mafrans.poppo.util.objects;

import me.mafrans.poppo.util.GUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static Manga make(JSONObject object) throws ParseException {
        Manga manga = new Manga();
        manga.setChapters(object.getInt("chapters"));
        manga.setVolumes(object.getInt("volumes"));
        manga.setPublished(object.getBoolean("publishing"));
        manga.setScore(object.getDouble("score"));
        manga.setTitle(object.getString("title"));
        manga.setSynopsis(object.getString("synopsis"));
        manga.setImageUrl(object.getString("image_url"));
        manga.setUrl(object.getString("url"));
        manga.setType(object.getString("type"));
        manga.setMembers(object.getInt("members"));
        manga.setMalId(object.getInt("mal_id"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        manga.setStartDate(null);
        manga.setEndDate(null);
        if(object.get("start_date") != null && object.get("start_date") instanceof String) {
            Date startDate = dateFormat.parse(object.getString("start_date").substring(0, 10));
            manga.setStartDate(startDate);
        }
        if(object.get("end_date") != null && object.get("end_date") instanceof String) {
            Date endDate = dateFormat.parse(object.getString("end_date").substring(0, 10));
            manga.setEndDate(endDate);
        }

        return manga;
    }

    public MessageEmbed getGenericEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(this.getTitle() + "(" + this.getType() + ")", this.getUrl(), "https://cdn.myanimelist.net/img/sp/icon/apple-touch-icon-256.png");
        embedBuilder.setThumbnail(this.getImageUrl());
        embedBuilder.addField("Synopsis", this.getSynopsis(), false);
        embedBuilder.addField("Professionally Published", this.isPublished() ? "Yes" : "No", true);

        if(this.getChapters() > 0 || this.getVolumes() > 0) {
            if(this.getVolumes() > 0) {
                embedBuilder.addField("Chapters", this.getVolumes() + " Volumes " + this.getChapters() + " Chapters", true);
            }
            else {
                embedBuilder.addField("Chapters", this.getChapters() + " Chapters", true);
            }
        }


        if(this.getStartDate() != null) {
            embedBuilder.addField("First Released", GUtil.DATE_TIME_FORMAT.format(this.getStartDate()).substring(0, 10), true);
        }
        if(this.getEndDate() != null) {
            embedBuilder.addField("Completed", GUtil.DATE_TIME_FORMAT.format(this.getEndDate()).substring(0, 10), true);
        }
        embedBuilder.addField("Score", this.getScore() + " / 10", true);
        embedBuilder.addField("Members", String.valueOf(this.getMembers()), true);
        embedBuilder.setColor(GUtil.randomColor());

        return embedBuilder.build();
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
