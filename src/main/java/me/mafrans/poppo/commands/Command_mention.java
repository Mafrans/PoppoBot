package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.restaction.RoleAction;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Command_mention implements ICommand {
    @Override
    public String getName() {
        return "mention";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.MODERATION, "Mentions users with arguments.", "mention <arguments...>", null, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        if(args.length == 0) {
            return false;
        }

        for(Role r : channel.getGuild().getRolesByName("[Poppo] Mentioned", true)) {
            r.delete().complete();
        }

        if(args[0].equalsIgnoreCase("everyone")) {
            if(!command.doOverride() && !command.getMessage().getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setAuthor("No Permission!", Main.config.httpd_url, command.getAuthor().getAvatarUrl());
                embedBuilder.setDescription("You need the MESSAGE_MENTION_EVERYONE permission to use this command!");
                channel.sendMessage(embedBuilder.build()).queue();
                return true;
            }

            List<Member> everyone = channel.getGuild().getMembers();
            List<Member> excludedMembers = new ArrayList<>();
            List<Role> excludedRoles = new ArrayList<>();

            for(String argument : ArrayUtils.subarray(args, 1, args.length)) {
                if(argument.startsWith("!")) {
                    String mention = argument.substring(1);
                    Member member = null;
                    Role role = null;
                    if(mention.startsWith("name:")) {
                        role = channel.getGuild().getRolesByName(mention.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getRolesByName(mention.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                        member = channel.getGuild().getMembersByName(mention.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getMembersByName(mention.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                    }
                    else {
                        member = channel.getGuild().getMemberById(mention);
                        role = channel.getGuild().getRoleById(mention);
                    }
                    if(member != null) {
                        excludedMembers.add(member);
                    }
                    if(role != null) {
                        excludedRoles.add(role);
                    }
                }
                else {
                    Member member = null;
                    Role role = null;
                    if(argument.startsWith("name:")) {
                        role = channel.getGuild().getRolesByName(argument.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getRolesByName(argument.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                        member = channel.getGuild().getMembersByName(argument.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getMembersByName(argument.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                    }
                    else {
                        member = channel.getGuild().getMemberById(argument);
                        role = channel.getGuild().getRoleById(argument);
                    }
                    if(member != null) {
                        everyone.add(member);
                    }
                    if(role != null) {
                        everyone.addAll(channel.getGuild().getMembersWithRoles(role));
                    }
                }

                RoleAction roleAction = channel.getGuild().getController().createRole();
                roleAction.setName("[Poppo] Mentioned").complete();
                roleAction.setMentionable(true).complete();
                Role role = roleAction.complete();
                command.getMessage().delete().queue();
                for(Member member : everyone) {
                    for(Role r : channel.getGuild().getRolesByName("[Poppo] Mentioned", true)) {
                        channel.getGuild().getController().removeRolesFromMember(member, r).complete();
                    }
                }
                outer: for(Member member : everyone) {
                    if(excludedMembers.contains(member)) continue;
                    for(Role r : excludedRoles) {
                        if(member.getRoles().contains(r)) {
                            continue outer;
                        }
                    }
                    channel.getGuild().getController().addRolesToMember(member, role).complete();
                }
                channel.sendMessage(role.getAsMention()).complete();
                for(Member member : everyone) {
                    for(Role r : channel.getGuild().getRolesByName("[Poppo] Mentioned", true)) {
                        channel.getGuild().getController().removeRolesFromMember(member, r).complete();
                    }
                }
            }
        }
        else if(args[0].equalsIgnoreCase("here")) {
            if(!command.doOverride() && !command.getMessage().getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setAuthor("No Permission!", Main.config.httpd_url, command.getAuthor().getAvatarUrl());
                embedBuilder.setDescription("You need the MESSAGE_MENTION_EVERYONE permission to use this command!");
                embedBuilder.setColor(new Color(175, 0, 0));
                channel.sendMessage(embedBuilder.build()).queue();
                return true;
            }

            List<Member> everyone = channel.getMembers();
            List<Member> excludedMembers = new ArrayList<>();
            List<Role> excludedRoles = new ArrayList<>();

            for(String argument : ArrayUtils.subarray(args, 1, args.length)) {
                if(argument.startsWith("!")) {
                    String mention = argument.substring(1);
                    Member member = null;
                    Role role = null;
                    if(mention.startsWith("name:")) {
                        role = channel.getGuild().getRolesByName(mention.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getRolesByName(mention.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                        member = channel.getGuild().getMembersByName(mention.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getMembersByName(mention.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                    }
                    else {
                        member = channel.getGuild().getMemberById(mention);
                        role = channel.getGuild().getRoleById(mention);
                    }
                    if(member != null) {
                        excludedMembers.add(member);
                    }
                    if(role != null) {
                        excludedRoles.add(role);
                    }
                }
                else {
                    Member member = null;
                    Role role = null;
                    if(argument.startsWith("name:")) {
                        role = channel.getGuild().getRolesByName(argument.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getRolesByName(argument.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                        member = channel.getGuild().getMembersByName(argument.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getMembersByName(argument.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                    }
                    else {
                        member = channel.getGuild().getMemberById(argument);
                        role = channel.getGuild().getRoleById(argument);
                    }
                    if(member != null) {
                        everyone.add(member);
                    }
                    if(role != null) {
                        everyone.addAll(channel.getGuild().getMembersWithRoles(role));
                    }
                }

                RoleAction roleAction = channel.getGuild().getController().createRole();
                roleAction.setName("[Poppo] Mentioned").complete();
                roleAction.setMentionable(true).complete();
                Role role = roleAction.complete();
                command.getMessage().delete().queue();
                for(Member member : everyone) {
                    for(Role r : channel.getGuild().getRolesByName("[Poppo] Mentioned", true)) {
                        channel.getGuild().getController().removeRolesFromMember(member, r).complete();
                    }
                }
                outer: for(Member member : everyone) {
                    if(excludedMembers.contains(member)) continue;
                    for(Role r : excludedRoles) {
                        if(member.getRoles().contains(r)) {
                            continue outer;
                        }
                    }
                    channel.getGuild().getController().addRolesToMember(member, role).complete();
                }
                channel.sendMessage(role.getAsMention()).complete();
                for(Member member : everyone) {
                    for(Role r : channel.getGuild().getRolesByName("[Poppo] Mentioned", true)) {
                        channel.getGuild().getController().removeRolesFromMember(member, r).complete();
                    }
                }
            }
        }
        else {
            Role mentionRole = null;
            if(args[0].startsWith("name:")) {
                mentionRole = channel.getGuild().getRolesByName(args[0].replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getRolesByName(args[0].replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
            }

            if(!command.doOverride() && !command.getMessage().getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setAuthor("No Permission!", Main.config.httpd_url, command.getAuthor().getAvatarUrl());
                embedBuilder.setDescription("You need the MESSAGE_MENTION_EVERYONE permission to use this command!");
                embedBuilder.setColor(new Color(175, 0, 0));
                channel.sendMessage(embedBuilder.build()).queue();
                return true;
            }

            List<Member> everyone = new ArrayList<>();
            for(Member member : channel.getGuild().getMembers()) {
                if(member.getRoles().contains(mentionRole)) {
                    everyone.add(member);
                }
            }
            List<Member> excludedMembers = new ArrayList<>();
            List<Role> excludedRoles = new ArrayList<>();

            for(String argument : ArrayUtils.subarray(args, 1, args.length)) {
                if(argument.startsWith("!")) {
                    String mention = argument.substring(1);
                    Member member = null;
                    Role role = null;
                    if(mention.startsWith("name:")) {
                        role = channel.getGuild().getRolesByName(mention.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getRolesByName(mention.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                        member = channel.getGuild().getMembersByName(mention.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getMembersByName(mention.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                    }
                    else {
                        member = channel.getGuild().getMemberById(mention);
                        role = channel.getGuild().getRoleById(mention);
                    }
                    if(member != null) {
                        excludedMembers.add(member);
                    }
                    if(role != null) {
                        excludedRoles.add(role);
                    }
                }
                else {
                    Member member = null;
                    Role role = null;
                    if(argument.startsWith("name:")) {
                        role = channel.getGuild().getRolesByName(argument.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getRolesByName(argument.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                        member = channel.getGuild().getMembersByName(argument.replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getMembersByName(argument.replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
                    }
                    else {
                        member = channel.getGuild().getMemberById(argument);
                        role = channel.getGuild().getRoleById(argument);
                    }
                    if(member != null) {
                        everyone.add(member);
                    }
                    if(role != null) {
                        everyone.addAll(channel.getGuild().getMembersWithRoles(role));
                    }
                }

                RoleAction roleAction = channel.getGuild().getController().createRole();
                roleAction.setName("[Poppo] Mentioned").complete();
                roleAction.setMentionable(true).complete();
                Role role = roleAction.complete();
                command.getMessage().delete().queue();
                for(Member member : everyone) {
                    for(Role r : channel.getGuild().getRolesByName("[Poppo] Mentioned", true)) {
                        channel.getGuild().getController().removeRolesFromMember(member, r).complete();
                    }
                }
                outer: for(Member member : everyone) {
                    if(excludedMembers.contains(member)) continue;
                    for(Role r : excludedRoles) {
                        if(member.getRoles().contains(r)) {
                            continue outer;
                        }
                    }
                    channel.getGuild().getController().addRolesToMember(member, role).complete();
                }
                channel.sendMessage(role.getAsMention()).complete();
                for(Member member : everyone) {
                    for(Role r : channel.getGuild().getRolesByName("[Poppo] Mentioned", true)) {
                        channel.getGuild().getController().removeRolesFromMember(member, r).complete();
                    }
                }
            }
        }
        return true;
    }

}
