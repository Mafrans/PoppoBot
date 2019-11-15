package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.CommandHandler;
import me.mafrans.poppo.util.Feature;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.Zork;
import me.mafrans.poppo.util.objects.Rank;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ZorkListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        System.out.println("Message Received");
        if(event.getAuthor().isBot()) return;

        if(event.getChannel().getType() == ChannelType.TEXT) {
            System.out.println("Channel is Text");
            for(Zork zork : Zork.registeredInstances) {
                if(zork.channel.getId().equals(event.getTextChannel().getId())) {
                    System.out.println("Sending Zork Event");
                    zork.onInput(event.getMessage().getContentDisplay());
                }
            }
        }
    }
}
