package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandHandler;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.StringFormatter;
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
                "Shows a list of all commands and their usages.",
                "help [command]",
                Arrays.asList("commands"),
                false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        if(command.getArgs().length == 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("**Command List:**\n");
            stringBuilder.append("```");
            for(ICommand cmd : CommandHandler.getCommands()) {
                stringBuilder.append(" - " + cmd.getName() + ": " + cmd.getMeta().getDescription() + "\n");
            }
            stringBuilder.append("```");

            channel.sendMessage(stringBuilder.toString()).queue();
            return true;
        }

        for(ICommand cmd : CommandHandler.getCommands()) {
            if(command.getArgs()[0].equalsIgnoreCase(cmd.getName())) {
                channel.sendMessage(StringFormatter.parseLines(new String[] {
                        "Help for command **" + cmd.getName() + "**",
                        "```lua",
                        "Description: \"" + cmd.getMeta().getDescription() + "\"",
                        "Usage: \"" + cmd.getMeta().getUsage() + "\"",
                        cmd.getMeta().getAliases() != null ? "Aliases: \"" + StringFormatter.arrayToString(cmd.getMeta().getAliases().toArray(), ",%s") : "",
                        "```"
                })).queue();
                return true;
            }
        }
        return false;
    }
}
