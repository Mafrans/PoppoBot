package me.mafrans.poppo.util;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import sun.nio.cs.UnicodeEncoder;

import java.util.ArrayList;
import java.util.List;

public class SelectionList {
    public static List<SelectionList> openLists = new ArrayList<>();

    private String title;
    private List<String> alternatives = new ArrayList<>();
    private List<Runnable> outputs = new ArrayList<>();
    private Message message;
    private TextChannel channel;

    public SelectionList(String title, TextChannel channel, List<String> alternatives, List<Runnable> outputs) {
        this.title = title;
        this.channel = channel;
        this.alternatives = alternatives;
        this.outputs = outputs;
    }

    public SelectionList(String title, TextChannel channel) {
        this.title = title;
        this.channel = channel;
    }

    public void addAlternative(String title, Runnable output) {
        alternatives.add(title);
        outputs.add(output);
    }

    public void show(TextChannel channel) {
        StringBuilder builder = new StringBuilder();

        builder.append("**" + title + "**");
        builder.append("\n");

        int i = 1;
        for(String alternative : alternatives) {
            if(i > 10) break; // should only display 10 at a time

            builder.append("\n" + i + " - " + alternative);
            i++;
        }

        message = channel.sendMessage(builder.toString()).complete();
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
}
