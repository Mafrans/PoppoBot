package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;

public class Command_avatar implements ICommand {
    @Override
    public String getName() {
        return "avatar";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.UTILITY, "Gets the avatar of a user.", "avatar <user>", new String[] {"profilepic", "profilepicture", "picture", "image", "pfp"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        String uuid;
        if(args.length < 2) {
            uuid = command.getAuthor().getId();
        }
        else {
            if (args[1].length() == 18 && NumberUtils.isDigits(args[1])) {
                uuid = args[1];
            } else if (command.getMessage().getMentionedUsers().size() > 0) {
                uuid = command.getMessage().getMentionedUsers().get(0).getId();
            } else if (Main.jda.getUsersByName(StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " "), true).size() > 0) {
                uuid = Main.jda.getUsersByName(StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " "), true).get(0).getId();
            } else {
                channel.sendMessage("Could not find a user with that name or id.").queue();
                return true;
            }
        }

        User user = Main.jda.getUserById(uuid);
        if (user == null) {
            channel.sendMessage("Could not find a user with that name or id.").queue();
            return true;
        }
        String avatarUrl = "https://cdn.discordapp.com/avatars/" + uuid + "/" + user.getAvatarId() + ".gif?size=2048";
        Image image = ImageIO.read(new URL(avatarUrl));
        if(image == null) {
            avatarUrl = "https://cdn.discordapp.com/avatars/" + uuid + "/" + user.getAvatarId() + ".png?size=2048";
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setTitle(user.getName() + "'s Avatar", avatarUrl);
        embedBuilder.setImage(avatarUrl);

        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }
}
