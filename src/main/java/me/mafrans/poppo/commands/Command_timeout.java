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

public class Command_timeout implements ICommand {
    @Override
    public String getName() {
        return "timeout";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.MODERATION, "Times a user out for a set amount of time, effectively banning them.", "timeout <user> [time]", Arrays.asList("tempban"), false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {

        if(!command.doOverride() && !command.getMessage().getMember().hasPermission(Permission.BAN_MEMBERS)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor("No Permission!", Main.config.httpd_url, command.getAuthor().getAvatarUrl());
            embedBuilder.setDescription("You need the BAN_MEMBERS permission to use this command!");
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

            channel.getGuild().getController().addRolesToMember(target, Rank.TIMED_OUT.getRole(channel.getGuild())).queue();
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

                    guild.getController().removeRolesFromMember(target, Rank.TIMED_OUT.getRole(guild)).queue();
                    target.getUser().openPrivateChannel().complete().sendMessage("You are no longer timed out in **" + guild.getName() + "**").queue();
                    ((TextChannel)arguments[3]).sendMessage(target.getAsMention() + " is no longer timed out.").queue();
                }
            });

            thread.start();

            channel.sendMessage("I've timed " + target.getAsMention() + " out for you!").queue();
            target.getUser().openPrivateChannel().complete().sendMessage("You have been timed out in **" + channel.getGuild().getName() + "**. This means you will not be able to read or write any messages until **" + GUtil.DATE_TIME_FORMAT.format(dateOffset) + "**. I will notify you once you are able to read and write again!\n\n*If you think this timeout was unfair, please contact one of the moderators of **" + channel.getGuild().getName() + "** so they can release you from the timeout, you can still see their names in the member list!*").queue();
            return true;
        }

        System.out.println(target.getRoles());

        if(!Rank.MUTED.hasRole(target)) {
            channel.getGuild().getController().addRolesToMember(target, Rank.TIMED_OUT.getRole(channel.getGuild())).queue();
            channel.sendMessage("I've timed " + target.getAsMention() + " out for you!").queue();
            target.getUser().openPrivateChannel().complete().sendMessage("You have been timed out in **" + channel.getGuild().getName() + "**. This means you will not be able to read or write any messages!\n\n*If you think this timeout was unfair, please contact one of the moderators of **" + channel.getGuild().getName() + "** so they can release you from the timeout, you can still see their names in the member list!*").queue();
        }
        else {
            channel.getGuild().getController().removeRolesFromMember(target, Rank.TIMED_OUT.getRole(channel.getGuild())).queue();
            target.getUser().openPrivateChannel().complete().sendMessage("You're no longer timed out in **" + channel.getGuild().getName() + "**").queue();
            channel.sendMessage(target.getAsMention() + " is no longer timed out.").queue();
        }

        return true;
    }
}
