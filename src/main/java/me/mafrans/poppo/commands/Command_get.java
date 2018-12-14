package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

public class Command_get implements ICommand {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.UTILITY, "Gets information from discord.", "get <avatar|server> [args...]", new ArrayList<>(), false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        if(args.length == 0) {
            return false;
        }

        switch(args[0].toLowerCase()) {
            case "avatar":
                return getAvatar(command, channel);

            case "server":
            case "guild":
                return getServer(command, channel);
        }

        return true;
    }


    boolean getServer(Command command, TextChannel channel) {
        String[] args = command.getArgs();
        if(args.length < 2) {
            Guild guild = channel.getGuild();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setThumbnail(guild.getIconUrl());
            embedBuilder.setTitle(guild.getName());
            embedBuilder.addField("Members", String.valueOf(guild.getMembers().size()), true);
            embedBuilder.addField("Owner", String.valueOf(guild.getOwner().getAsMention()), true);
            embedBuilder.addField("Creation Date", String.valueOf(guild.getCreationTime()), true);
            embedBuilder.addField("ID", String.valueOf(guild.getId()), true);
            embedBuilder.addField("Icon Url", String.valueOf(guild.getIconUrl()), true);
            embedBuilder.addField("Emotes", String.valueOf(guild.getEmotes().size()), true);
            embedBuilder.setColor(GUtil.randomColor());

            channel.sendMessage(embedBuilder.build()).queue();
            return true;
        }

        Guild guild = Main.jda.getGuildById(args[1]);
        if(guild == null) {
            channel.sendMessage("Could not find a Server with that ID.").queue();
            return true;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(guild.getIconUrl());
        embedBuilder.setTitle(guild.getName());
        embedBuilder.addField("Members", String.valueOf(guild.getMembers().size()), true);
        embedBuilder.addField("Owner", String.valueOf(guild.getOwner().getAsMention()), true);
        embedBuilder.addField("Creation Date", String.valueOf(guild.getCreationTime()), true);
        embedBuilder.addField("ID", String.valueOf(guild.getId()), true);
        embedBuilder.addField("Icon Url", String.valueOf(guild.getIconUrl()), true);
        embedBuilder.addField("Emotes", String.valueOf(guild.getEmotes().size()), true);
        embedBuilder.setColor(GUtil.randomColor());

        channel.sendMessage(embedBuilder.build()).queue();
        return true;
    }


    boolean getAvatar(Command command, TextChannel channel) {
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
        String avatarUrl = "https://cdn.discordapp.com/avatars/" + uuid + "/" + user.getAvatarId() + ".png?size=2048";

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setTitle(user.getName() + "'s Avatar", avatarUrl);
        embedBuilder.setImage(avatarUrl);

        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }
}
