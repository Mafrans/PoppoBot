package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.config.ServerPrefs;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class Command_config implements ICommand {
    @Override
    public String getName() {
        return "config";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                CommandCategory.MODERATION,
                "Links information to the bot.",
                "config set|list <key> <value>",
                null,
                true);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();

        if(args[0].equalsIgnoreCase("set")) {
            if(args.length < 3) return false;

            ServerPrefs.valueOf(args[1].toUpperCase());
            ServerPrefs entry = ServerPrefs.valueOf(args[1].toUpperCase());
            String value = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ");

            entry.setString(channel.getGuild(), value);
            channel.sendMessage("Set config entry " + entry.toString() + " to: \"" + value + "\"").queue();
            return true;
        }

        if(args[0].equalsIgnoreCase("remove")) {
            if(args.length != 2) return false;

            ServerPrefs.valueOf(args[1].toUpperCase());
            ServerPrefs entry = ServerPrefs.valueOf(args[1].toUpperCase());

            entry.remove(channel.getGuild());
            channel.sendMessage("Removed config entry " + entry.toString()).queue();
            return true;
        }

        if(args[0].equalsIgnoreCase("list")) {
            if(args.length != 1) return false;
            StringBuilder builder = new StringBuilder();
            builder.append("**List of config entries:**\n");

            for(ServerPrefs entry : ServerPrefs.values()) {
                builder.append("\n`")
                        .append(entry.toString().replace(" ", "_"))
                        .append("` - ").append(entry.getInfo());
            }

            channel.sendMessage(builder.toString()).queue();
            return true;
        }

        return false;
    }
}
