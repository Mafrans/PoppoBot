package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
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
        else if(Main.jda.getUsersByName(StringUtils.join(args, " "), true).size() > 0) {
            uuid = Main.jda.getUsersByName(StringUtils.join(args, " "), true).get(0).getId();
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
        String avatarUrl = "https://cdn.discordapp.com/avatars/" + uuid + "/" + user.getAvatarId() + ".png?size=2048";

        MessageEmbed embed = new EmbedBuilder()
                .setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .setTitle(user.getName() + "#" + user.getDiscriminator() + ":")
                .setThumbnail(avatarUrl)
                .addField("Names", StringUtils.join(userData.getNames().toArray(new String[0]), ", "), false)
                .addField("UUID", userData.getUuid(), false)
                .addField("Last Online", userData.getLastOnlineTag(), false)
                .addField("Avatar URL", avatarUrl, false).build();

        channel.sendMessage(embed).queue();

        return true;
    }
}
