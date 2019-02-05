package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.Id;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

@Id("commands::config")
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
        if(!Main.serverPrefs.getGuilds().contains(channel.getGuild())) {
            channel.sendMessage("This Server does not seem to have a config file, please contact @Mafrans1234!").queue();
        }
        JSONObject prefs = Main.serverPrefs.getPreferences(channel.getGuild());

        if(args[0].equalsIgnoreCase("set")) {
            if(args.length < 3) return false;
            String value = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ");
            prefs.put(args[1].toLowerCase(), value);
            Main.serverPrefs.savePreferences(channel.getGuild(), prefs);
            channel.sendMessage("Set config entry " + args[1].toLowerCase() + " to: \"" + value + "\"").queue();
            return true;
        }

        if(args[0].equalsIgnoreCase("remove")) {
            if(args.length != 2) return false;

            prefs.put(args[1].toLowerCase(), "");
            Main.serverPrefs.savePreferences(channel.getGuild(), prefs);
            channel.sendMessage("Removed config entry " + args[1].toLowerCase()).queue();
            return true;
        }

        if(args[0].equalsIgnoreCase("list")) {
            if(args.length != 1) return false;
            StringBuilder builder = new StringBuilder();
            builder.append("**List of config entries:**\n");

            for(String entry : prefs.keySet()) {
                builder.append("\n`").append(entry).append("`");
            }

            channel.sendMessage(builder.toString()).queue();
            return true;
        }

        return false;
    }
}
