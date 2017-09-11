package me.mafrans.poppo.listeners;

import me.mafrans.poppo.commands.Command;
import me.mafrans.poppo.commands.CommandHandler;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getChannel().getType() == ChannelType.TEXT) {
            final Command command = CommandHandler.parseCommand(event.getMessage());
            if(command == null) return;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(!command.getExecutor().onCommand(command, event.getTextChannel())) {
                            event.getTextChannel().sendMessage("Correct usage for command " + command.getCmd() + " is: `" + command.getExecutor().getMeta().getUsage() + "`").queue();
                        }
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }
    }
}
