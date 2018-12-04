package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.StringFormatter;
import me.mafrans.poppo.util.config.DataUser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class Command_identify implements ICommand {
    @Override
    public String getName() {
        return "identify";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                CommandCategory.UTILITY,
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

        DataUser userData = Main.userList.getUsersFrom("uuid", uuid).get(0);

        if(userData == null) {
            channel.sendMessage("Couldn't find any cached user with the id of \"" + uuid + "\"").queue();
            return true;
        }


        User user = Main.jda.getUserById(uuid);
        Random random = new Random();

        MessageEmbed embed = new EmbedBuilder()
                .setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .setTitle(user.getName() + "#" + user.getDiscriminator() + ":")
                .setThumbnail(user.getAvatarUrl())
                .addField("Names", StringUtils.join(userData.getNames().toArray(new String[]{}), ", "), false)
                .addField("UUID", userData.getUuid(), false)
                .addField("Last Online", userData.getLastOnlineTag(), false)
                .addField("Avatar URL", userData.getAvatarUrl(), false).build();

        StringBuilder stringBuilder = new StringBuilder();
        String[] unparsedString = new String[] {
                "```lua",
                "Names: " + StringFormatter.arrayToString(userData.getNames().toArray(new String[]{})),
                "UUID: \'" + userData.getUuid() + "\'",
                "Last Online: \'" + userData.getLastOnlineTag() + "\'",
                "Avatar URL: \'" + userData.getAvatarUrl() + "\'",
                "```"
            };
        stringBuilder.append(StringFormatter.parseLines(unparsedString));

        channel.sendMessage(embed).queue();

        return true;
    }
}
