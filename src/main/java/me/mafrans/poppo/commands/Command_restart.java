package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Arrays;

public class Command_restart implements ICommand {
    @Override
    public String getName() {
        return "restart";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.UTILITY, "Restarts the bot.", "restart", new String[]{"reload"}, false, true);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        /*
        if(!command.doOverride() && !Main.config.overlord_users.contains(command.getAuthor().getId())) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor("No Permission!", Main.config.httpd_url, command.getAuthor().getAvatarUrl());
            embedBuilder.setDescription("You need the OVERLORD permission to use this command!");
            embedBuilder.setColor(new Color(175, 0, 0));
            channel.sendMessage(embedBuilder.build()).queue();
            return true;
        }

        channel.sendMessage("Restarting!").complete();
        Main.jda.getPresence().setStatus(OnlineStatus.OFFLINE);

        Main.restartApplication(()->{});
        */
        return true;
    }
}
