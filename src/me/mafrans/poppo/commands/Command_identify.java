package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.StringFormatter;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.math.NumberUtils;
import me.mafrans.poppo.util.config.DataUser;
import org.json.JSONArray;

import java.util.Arrays;

public class Command_identify implements ICommand {
    @Override
    public String getName() {
        return "identify";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                "Identifies a user.",
                "identify <user>",
                Arrays.asList("whos", "who's"),
                false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        if(args.length != 1) return false;
        String uuid = null;

        if(args[0].length() == 18 && NumberUtils.isDigits(args[0])) {
            uuid = args[0];
        }
        else if(command.getMessage().getMentionedUsers().size() > 0) {
            uuid = command.getMessage().getMentionedUsers().get(0).getId();
        }
        else if(Main.jda.getUsersByName(args[0], true).size() > 0) {
            uuid = Main.jda.getUsersByName(args[0], true).get(0).getId();
        }
        else {
            channel.sendMessage("Could not find a user with that name or id.").queue();
            return true;
        }

        DataUser userData = Main.userSpecificData.getUser(uuid) != null ? Main.userSpecificData.getUser(uuid) : null;

        if(userData == null) {
            channel.sendMessage("Couldn't find any cached user with the id of \"" + uuid + "\"").queue();
            return true;
        }

        StringBuilder stringBuilder = new StringBuilder();

        User user = Main.jda.getUserById(uuid);
        stringBuilder.append("**" + user.getName() + "#" + user.getDiscriminator() + ":**");

        String[] unparsedString = new String[] {
                "```lua",
                "Names: " + StringFormatter.arrayToString(userData.getNames().toArray(new String[]{}), "\'%s\'"),
                "UUID: \'" + userData.getUuid() + "\'",
                "Last Online: \'" + userData.getLastOnlineTag() + "\'",
                "```"
            };

        stringBuilder.append(StringFormatter.parseLines(unparsedString));

        channel.sendMessage(stringBuilder.toString()).queue();

        return true;
    }
}
