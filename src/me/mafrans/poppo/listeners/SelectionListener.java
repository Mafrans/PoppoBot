package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.SelectionList;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.math.NumberUtils;

public class SelectionListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor() == Main.jda.getSelfUser() || event.getChannelType() != ChannelType.TEXT) return;

        if(SelectionList.openLists.isEmpty()) return;
        for(SelectionList list : SelectionList.getOpenLists()) {
            if(list.getChannel() == event.getTextChannel()) {
                if(NumberUtils.isDigits(event.getMessage().getContent())) {
                    int index = Integer.parseInt(event.getMessage().getContent());

                    if(index > list.getAlternatives().size()) return;

                    Thread thread = new Thread(list.getOutputs().get(index - 1));
                    thread.start();
                }
                else {
                    list.getMessage().delete().queue();
                    return;
                }
                list.remove();
                try {
                    event.getMessage().delete().queue();
                }
                catch(Exception e) {
                    //Do nothing
                    break;
                }
                break;
            }
        }
    }
}