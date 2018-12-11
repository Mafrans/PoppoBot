package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.SelectionList;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.math.NumberUtils;

public class SelectionListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor() == Main.jda.getSelfUser() || event.getChannelType() != ChannelType.TEXT) return;

        if(SelectionList.openLists.isEmpty()) return;
        for(SelectionList list : SelectionList.getOpenLists()) {
            if(list.getChannel() == event.getTextChannel() && (list.getOwner() == null || list.getOwner().getId().equals(event.getAuthor().getId()))) {
                if(NumberUtils.isDigits(event.getMessage().getContentDisplay())) {
                    int index = Integer.parseInt(event.getMessage().getContentDisplay());

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
