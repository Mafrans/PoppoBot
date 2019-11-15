package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandHandler;
import me.mafrans.poppo.util.Feature;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.config.ServerPrefs;
import me.mafrans.poppo.util.objects.Rank;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class CommandListener extends ListenerAdapter {
    private Command command;
    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        if(event.getChannel().getType() == ChannelType.TEXT) {
            command = CommandHandler.parseCommand(event.getMessage());
            if (command == null) return;

            Guild guild = event.getMessage().getGuild();

            if(command.getExecutor().getClass().getAnnotation(Id.class) != null) {
                if(!Feature.isEnabled(guild, command.getExecutor().getClass())) {
                    return;
                }
            }

            if (!command.isOverride() && Main.config.overlord_users.contains(command.getAuthor().getId())) {
                if (command.getExecutor().getMeta().isBotCommanderOnly() && !Rank.getRank(event.getMember()).hasRank(Rank.BOT_COMMANDER)) {
                    return;
                }
            }

            Thread thread = new Thread(() -> {
                try {
                    if(!command.getExecutor().onCommand(command, event.getTextChannel())) {
                        event.getTextChannel().sendMessage("Correct usage for command " + command.getCmd() + " is: `" + command.getExecutor().getMeta().getUsage() + "`").queue();
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        }
    }
}
