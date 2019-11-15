package me.mafrans.poppo.util.objects;

import lombok.Data;
import me.mafrans.poppo.util.GUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Poll {
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
    public static List<Poll> openPolls = new ArrayList<>();

    public static String DOWN_VOTE = "\u274E";
    public static String UP_VOTE = "\u2705";

    private Message message;
    private String imageUrl;
    private User author;
    private String title;
    private String description;
    private int upVotes;
    private int downVotes;
    private Date endDate;
    private TextChannel channel;

    public Poll(User author, TextChannel channel) {
        this.channel = channel;
        this.author = author;
    }

    public Poll(User author, TextChannel channel,  String title, String description) {
        this.title = title;
        this.channel = channel;
        this.author = author;
        this.description = description;
    }

    public Poll(User author, TextChannel channel, String title, String description, Date endDate) {
        this.title = title;
        this.author = author;
        this.channel = channel;
        this.description = description;
        this.endDate = endDate;
    }

    public Poll(User author, TextChannel channel, String title, String description, Date endDate, String imageUrl) {
        this.title = title;
        this.author = author;
        this.channel = channel;
        this.description = description;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
    }

    public Poll(Message message, User author, String title, String description, int upVotes, int downVotes, Date endDate, String imageUrl) {
        this.title = title;
        this.message = message;
        this.author = author;
        this.description = description;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
    }

    public boolean hasEnded() {
        Date current = new Date();
        return current.getTime() > endDate.getTime();
    }

    public void showPoll(TextChannel channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        if(endDate != null) {
            embedBuilder.setFooter("Ends at " + GUtil.DATE_TIME_FORMAT.format(endDate), null);
        }
        if(imageUrl != null) {
            embedBuilder.setThumbnail(imageUrl);
        }

        message = channel.sendMessage(embedBuilder.build()).complete();
        message.addReaction(UP_VOTE).queue();
        message.addReaction(DOWN_VOTE).queue();
    }
}
