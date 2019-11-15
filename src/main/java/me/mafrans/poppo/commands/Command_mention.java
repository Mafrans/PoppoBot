package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.objects.Rank;
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

@Id("commands::mention")
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

        String roleName = "[Poppo] Mentioned";
        for(Role role : channel.getGuild().getRolesByName(roleName, false)) {
            role.delete().complete();
        }

        if(args[0].equalsIgnoreCase("everyone")) {

            if(Rank.requirePermission(command, Permission.MESSAGE_MENTION_EVERYONE)) {
                return true;
            }

            List<Member> everyone = channel.getGuild().getMembers();
            List<Member> excludedMembers = new ArrayList<>();
            List<Role> excludedRoles = new ArrayList<>();

            for(String argument : ArrayUtils.subarray(args, 1, args.length)) {
                if(argument.startsWith("!")) {
                    String mention = argument.substring(1);

                    Member member = getMember(mention, channel.getGuild());
                    Role role = getRole(mention, channel.getGuild());

                    if(member != null) excludedMembers.add(member);
                    if(role != null) excludedRoles.add(role);
                }
                else {
                    Member member = getMember(argument, channel.getGuild());
                    Role role = getRole(argument, channel.getGuild());

                    if(member != null) everyone.add(member);
                    if(role != null) everyone.addAll(channel.getGuild().getMembersWithRoles(role));
                }

                RoleAction roleAction = channel.getGuild().getController().createRole();
                roleAction.setName(roleName).complete();
                roleAction.setMentionable(true).complete();
                Role role = roleAction.complete();
                command.getMessage().delete().queue();

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
            }
        }
        else if(args[0].equalsIgnoreCase("here")) {

            if(Rank.requirePermission(command, Permission.MESSAGE_MENTION_EVERYONE)) {
                return true;
            }

            List<Member> everyone = channel.getMembers();
            List<Member> excludedMembers = new ArrayList<>();
            List<Role> excludedRoles = new ArrayList<>();

            for(String argument : ArrayUtils.subarray(args, 1, args.length)) {
                if(argument.startsWith("!")) {
                    String mention = argument.substring(1);

                    Member member = getMember(mention, channel.getGuild());
                    Role role = getRole(mention, channel.getGuild());

                    if(member != null) excludedMembers.add(member);
                    if(role != null) excludedRoles.add(role);
                }
                else {
                    Member member = getMember(argument, channel.getGuild());
                    Role role = getRole(argument, channel.getGuild());

                    if(member != null) everyone.add(member);
                    if(role != null) everyone.addAll(channel.getGuild().getMembersWithRoles(role));
                }

                RoleAction roleAction = channel.getGuild().getController().createRole();
                roleAction.setName("[Poppo] Mentioned").complete();
                roleAction.setMentionable(true).complete();
                Role role = roleAction.complete();
                command.getMessage().delete().queue();

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
            }
        }
        else {
            Role mentionRole = null;
            if(args[0].startsWith("name:")) {
                mentionRole = channel.getGuild().getRolesByName(args[0].replaceFirst("name:", "").replace("_", " "), true).size() > 0 ? channel.getGuild().getRolesByName(args[0].replaceFirst("name:", "").replace("_", " "), true).get(0) : null;
            }

            if(!command.isOverride() && !command.getMessage().getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
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

                    Member member = getMember(mention, channel.getGuild());
                    Role role = getRole(mention, channel.getGuild());

                    if(member != null) excludedMembers.add(member);
                    if(role != null) excludedRoles.add(role);
                }
                else {
                    Member member = getMember(argument, channel.getGuild());
                    Role role = getRole(argument, channel.getGuild());

                    if(member != null) everyone.add(member);
                    if(role != null) everyone.addAll(channel.getGuild().getMembersWithRoles(role));
                }

                RoleAction roleAction = channel.getGuild().getController().createRole();
                roleAction.setName(roleName).complete();
                roleAction.setMentionable(true).complete();
                Role role = roleAction.complete();
                command.getMessage().delete().queue();

                outer: for(Member member : everyone) {
                    if(excludedMembers.contains(member)) continue;
                    for(Role r : excludedRoles) {
                        if(member.getRoles().contains(r)) {
                            continue outer;
                        }
                    }
                    channel.getGuild().getController().addRolesToMember(member, role).complete();
                }
                channel.sendMessage(role.getAsMention()).queue();
            }
        }
        return true;
    }

    private Member getMember(String input, Guild guild) {
        Member member;
        List<Member> members = guild.getMembersByName(input.replaceFirst("name:", "").replace("_", " "), true);

        if(input.startsWith("name:")) {
            member = members.size() > 0 ? members.get(0) : null;
        }
        else {
            member = guild.getMemberById(input);
        }
        return member;
    }

    private Role getRole(String input, Guild guild) {
        Role role;
        List<Role> roles = guild.getRolesByName(input.replaceFirst("name:", "").replace("_", " "), true);
        if(input.startsWith("name:")) {
            role = roles.size() > 0 ? roles.get(0) : null;
        }
        else {
            role = guild.getRoleById(input);
        }
        return role;
    }
}
