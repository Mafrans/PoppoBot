package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.objects.Rank;
import me.mafrans.poppo.util.timedtasks.PoppoRunnable;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.Date;

public class Command_mute implements ICommand {
    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.MODERATION, "Mutes a user for a set amount of time.", "mute <user> [time]", Arrays.asList("stfu"), false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {

        if(!command.doOverride() && !command.getMessage().getMember().hasPermission(Permission.VOICE_MUTE_OTHERS)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor("No Permission!", Main.config.httpd_url, command.getAuthor().getAvatarUrl());
            embedBuilder.setDescription("You need the VOICE_MUTE_OTHERS permission to use this command!");
            embedBuilder.setColor(new Color(175, 0, 0));
            channel.sendMessage(embedBuilder.build()).queue();
            return true;
        }
        String[] args = command.getArgs();
        if(args.length < 1) {
            return false;
        }

        String uuid = "";
        if(args[0].length() == 18 && NumberUtils.isDigits(args[0])) {
            uuid = args[0];
        }
        else if(command.getMessage().getMentionedUsers().size() > 0) {
            uuid = command.getMessage().getMentionedUsers().get(0).getId();
        }
        else if(Main.jda.getUsersByName(args[0], true).size() > 0) {
            uuid = Main.jda.getUsersByName(args[0], true).get(0).getId();
        }

        User targetUser = Main.jda.getUserById(uuid);
        if(targetUser == null) {
            channel.sendMessage("Sorry, but I couldn't find that user.").queue();
            return true;
        }
        Member target = channel.getGuild().getMember(targetUser);

        if(args.length >= 1 + target.getEffectiveName().split(" ").length) {
            Date dateOffset = GUtil.parseDateOffset(StringUtils.join(ArrayUtils.subarray(args, target.getEffectiveName().split(" ").length, args.length)));
            if(dateOffset == null) {
                channel.sendMessage("That amount of time seems invalid. Try something like '1h 10min' instead!").queue();
                return true;
            }

            channel.getGuild().getController().addRolesToMember(target, Rank.MUTED.getRole(channel.getGuild())).queue();
            Thread thread = new Thread(new PoppoRunnable(new Object[]{dateOffset, target, channel.getGuild(), channel}) {
                @Override
                public void run() {
                    try {
                        Thread.sleep(((Date)arguments[0]).getTime() - new Date().getTime());
                    }
                    catch (InterruptedException e) {
                        // Do nothing;
                    }

                    Member target = (Member) arguments[1];
                    Guild guild = (Guild) arguments[2];

                    guild.getController().removeRolesFromMember(target, Rank.MUTED.getRole(guild)).queue();
                    target.getUser().openPrivateChannel().complete().sendMessage("You are no longer muted in **" + guild.getName() + "**").queue();
                    ((TextChannel)arguments[3]).sendMessage(target.getAsMention() + " is no longer muted.").queue();
                }
            });

            thread.start();

            channel.sendMessage("I've muted " + target.getAsMention() + " for you!").queue();
            return true;
        }

        System.out.println(target.getRoles());

        if(!Rank.MUTED.hasRole(target)) {
            channel.getGuild().getController().addRolesToMember(target, Rank.MUTED.getRole(channel.getGuild())).queue();
            channel.sendMessage("I've muted " + target.getAsMention() + " for you!").queue();
        }
        else {
            channel.getGuild().getController().removeRolesFromMember(target, Rank.MUTED.getRole(channel.getGuild())).queue();
            target.getUser().openPrivateChannel().complete().sendMessage("You're no longer muted in **" + channel.getGuild().getName() + "**").queue();
            channel.sendMessage(target.getAsMention() + " is no longer muted.").queue();
        }

        return true;
    }
}
