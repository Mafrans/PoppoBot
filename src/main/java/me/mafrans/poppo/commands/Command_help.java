package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.*;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.StringFormatter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;

public class Command_help implements ICommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                CommandCategory.UTILITY,
                "Shows a list of all commands and their usages.",
                "help [command]",
                new String[] {"commands"},
                false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        if(command.getArgs().length == 0) {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("**Command List:**\n");
            //embedBuilder.setTitle("Command List");
            embedBuilder.setAuthor("Command List", "http://poppobot.ga/help", Main.jda.getSelfUser().getAvatarUrl());
            embedBuilder.setColor(GUtil.randomColor());
            embedBuilder.addField("Command Prefix:", Main.config.command_prefix + "\n\u00AD", false);
            stringBuilder.append("```");
            for(CommandCategory commandCategory : CommandCategory.values()) {
                StringBuilder content = new StringBuilder();
                for (ICommand cmd : CommandHandler.getCommands()) {
                    if(cmd.getMeta().isHidden()) continue;
                    if(cmd.getMeta().getCategory() == commandCategory) {
                        content.append("**").append(GUtil.capitalize(cmd.getName())).append(":** ").append(cmd.getMeta().getDescription()).append("\n");
                    }
                }

                embedBuilder.addField(commandCategory.getEmote() + commandCategory.getName(), content + "\n\u00AD", false);
            }
            stringBuilder.append("```");

            channel.sendMessage(embedBuilder.build()).queue();
            return true;
        }

        for(ICommand cmd : CommandHandler.getCommands()) {
            if(command.getArgs()[0].equalsIgnoreCase(cmd.getName())) {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        //.setTitle("Help for command " + cmd.getName())
                        .setColor(GUtil.randomColor())
                        .addField("Description", cmd.getMeta().getDescription(), false)
                        .addField("Usage", cmd.getMeta().getUsage(), false)
                        .setAuthor("Help for command: " + GUtil.capitalize(cmd.getName()), "http://poppobot.ga/help?command=" + cmd.getName(), Main.jda.getSelfUser().getAvatarUrl());

                if(cmd.getMeta().getAliases() != null && cmd.getMeta().getAliases().length > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String alias : cmd.getMeta().getAliases()) {
                        stringBuilder.append("- " + alias + "\n");
                    }

                    embedBuilder.addField("Aliases", stringBuilder.toString(), false);
                }

                channel.sendMessage(embedBuilder.build()).queue();

                /*channel.sendMessage(StringFormatter.parseLines(new String[] {
                        "Help for command **" + cmd.getName() + "**",
                        "```lua",
                        "Description: \"" + cmd.getMeta().getDescription() + "\"",
                        "Usage: \"" + cmd.getMeta().getUsage() + "\"",
                        cmd.getMeta().getAliases() != null ? "Aliases: \"" + StringFormatter.arrayToString(cmd.getMeta().getAliases().toArray(new String[0])) : "",
                        "```"
                })).queue();*/
                return true;
            }
        }
        return true;
    }
}
