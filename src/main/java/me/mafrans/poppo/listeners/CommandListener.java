package me.mafrans.poppo.listeners;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandHandler;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        if(event.getChannel().getType() == ChannelType.TEXT) {
            final Command command = CommandHandler.parseCommand(event.getMessage());
            if(command == null) return;

            if(command.getExecutor().getMeta().isBotCommanderOnly() && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                return;
            }

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
