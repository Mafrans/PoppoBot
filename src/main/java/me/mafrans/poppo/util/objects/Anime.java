package me.mafrans.poppo.util.objects;

import lombok.Data;
import me.mafrans.poppo.util.GUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static Anime make(JSONObject object) throws ParseException {
        Anime anime = new Anime();
        anime.setAiring(object.getBoolean("airing"));
        anime.setEpisodes(object.getInt("episodes"));
        anime.setScore(object.getDouble("score"));
        anime.setTitle(object.getString("title"));
        anime.setSynopsis(object.getString("synopsis"));
        anime.setImageUrl(object.getString("image_url"));
        anime.setUrl(object.getString("url"));
        anime.setType(object.getString("type"));
        anime.setMembers(object.getInt("members"));
        anime.setRating(null);
        if(object.get("rated") instanceof String) {
            anime.setRating(object.getString("rated"));
        }
        anime.setMalId(object.getInt("mal_id"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        anime.setStartDate(null);
        anime.setEndDate(null);
        if(object.get("start_date") != null && object.get("start_date") instanceof String) {
            Date startDate = dateFormat.parse(object.getString("start_date").substring(0, 10));
            anime.setStartDate(startDate);
        }
        if(object.get("end_date") != null && object.get("end_date") instanceof String) {
            Date endDate = dateFormat.parse(object.getString("end_date").substring(0, 10));
            anime.setEndDate(endDate);
        }
        return anime;
    }

    public MessageEmbed getGenericEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(this.getTitle() + "(" + this.getType() + ")" + (this.getRating() != null ? " [" + this.getRating() + "]" : ""), this.getUrl(), "https://cdn.myanimelist.net/img/sp/icon/apple-touch-icon-256.png");
        embedBuilder.setThumbnail(this.getImageUrl());
        embedBuilder.addField("Synopsis", this.getSynopsis(), false);
        embedBuilder.addField("Airing", this.isAiring() ? "Yes" : "No", true);
        embedBuilder.addField("Episodes", String.valueOf(this.getEpisodes()), true);
        if(this.getStartDate() != null) {
            embedBuilder.addField("First Aired", GUtil.DATE_TIME_FORMAT.format(this.getStartDate()).substring(0, 10), true);
        }
        if(this.getEndDate() != null) {
            embedBuilder.addField("Stopped Airing", GUtil.DATE_TIME_FORMAT.format(this.getEndDate()).substring(0, 10), true);
        }
        embedBuilder.addField("Score", this.getScore() + " / 10", true);
        embedBuilder.addField("Members", String.valueOf(this.getMembers()), true);
        embedBuilder.setColor(GUtil.randomColor());

        return embedBuilder.build();
    }
}
