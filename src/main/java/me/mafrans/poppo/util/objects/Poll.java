package me.mafrans.poppo.util.objects;

import me.mafrans.poppo.util.GUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Poll {
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
    public static List<Poll> openPolls = new ArrayList<>();

    public static String UP_VOTE = "\u274E";
    public static String DOWN_VOTE = "\u2705";

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

    public TextChannel getChannel() {
        return channel;
    }

    public void setChannel(TextChannel channel) {
        this.channel = channel;
    }

    public boolean hasEnded() {
        Date current = new Date();
        return current.getTime() > endDate.getTime();
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUpVotes() {
        /*
        int c = 0;
        for(MessageReaction reaction : message.getReactions()) {
            if(reaction.getReactionEmote().toString().equals(DOWN_VOTE)) {
                for (User user : reaction.getUsers()) {
                    if(!user.isBot()) {
                        c++;
                    }
                }
            }
        }
        return c;
        */
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public void changeUpVotes(int amount) {
        this.upVotes += amount;
    }

    public int getDownVotes() {
        /*
        int c = 0;
        for(MessageReaction reaction : message.getReactions()) {
            if(reaction.getReactionEmote().toString().equals(DOWN_VOTE)) {
                for (User user : reaction.getUsers()) {
                    if(!user.isBot()) {
                        c++;
                    }
                }
            }
        }
        return c;
        */
        return downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    public void changeDownVotes(int amount) {
        this.downVotes += amount;
    }

    public int getTotalVotes() {
        return upVotes - downVotes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
