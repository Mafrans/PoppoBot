package me.mafrans.poppo.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;

public class SelectionList {
    public static List<SelectionList> openLists = new ArrayList<>();

    private String title;
    private List<String> alternatives = new ArrayList<>();
    private List<Runnable> outputs = new ArrayList<>();
    private Message message;
    private TextChannel channel;
    private User owner;

    public SelectionList(String title, TextChannel channel, User owner, List<String> alternatives, List<Runnable> outputs) {
        this.title = title;
        this.channel = channel;
        this.alternatives = alternatives;
        this.outputs = outputs;
        this.owner = owner;
    }

    public SelectionList(String title, TextChannel channel, User owner) {
        this.title = title;
        this.channel = channel;
        this.owner = owner;
    }

    public void addAlternative(String title, Runnable output) {
        alternatives.add(title);
        outputs.add(output);
    }

    public void show(TextChannel channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("**" + title + "**");
        embedBuilder.setColor(GUtil.randomColor());

        int i = 1;
        for(String alternative : alternatives) {
            if(i > 10) break; // should only display 10 at a time

            embedBuilder.addField(String.valueOf(i), alternative, false);
            i++;
        }

        message = channel.sendMessage(embedBuilder.build()).complete();
        SelectionList.openLists.add(this);
    }

    public void remove() {
        SelectionList.openLists.remove(this);
    }

    public String getTitle() {
        return title;
    }

    public List<Runnable> getOutputs() {
        return outputs;
    }

    public static List<SelectionList> getOpenLists() {
        return openLists;
    }

    public List<String> getAlternatives() {
        return alternatives;
    }

    public Message getMessage() {
        return message;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
