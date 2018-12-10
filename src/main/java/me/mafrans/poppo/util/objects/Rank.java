package me.mafrans.poppo.util.objects;

import lombok.Getter;
import me.mafrans.poppo.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.managers.RoleManager;
import net.dv8tion.jda.core.requests.restaction.RoleAction;

import java.awt.*;
import java.util.HashMap;

public enum Rank {
    BOT_COMMANDER("Bot Commander", new Color(30, 170, 170), new Object[0][0], true),

    MEMBER("Member", Color.WHITE, new Object[][]{}, false),

    MUTED("Muted", new Color(120, 120, 120), new Object[][] {{Permission.MESSAGE_WRITE, false}}, true),
    TIMED_OUT("Timed Out", new Color(120, 120, 120), new Object[][] {{Permission.MESSAGE_WRITE, false}, {Permission.MESSAGE_READ, false}}, true)
    ;



    private @Getter String name;
    private @Getter Color color;
    private @Getter HashMap<Guild, Role> roleMap;
    private @Getter HashMap<Permission, Boolean> permissions;
    private boolean initialize;

    Rank(String name, Color color, Object[][] permissions, boolean initialize) {
        this.name = name;
        this.color = color;

        if(permissions != null) {
            this.permissions = new HashMap<Permission, Boolean>(permissions.length);
            for (Object[] mapping : permissions)
            {
                this.permissions.put((Permission) mapping[0], (boolean) mapping[1]);
            }


        }
        this.roleMap = new HashMap<>();
        this.initialize = initialize;
    }

    public void initialize() {
        if(!this.doInitialize()) return;

        for (Guild guild : Main.jda.getGuilds()) {
            try {
                System.out.println("Initializing " + this.name + " for guild " + guild.getName());
                if (guild.getRolesByName(name, false).size() == 1) {
                    this.roleMap.put(guild, guild.getRolesByName(name, false).get(0));
                }
                else if (guild.getRolesByName(name, false).size() > 1) {
                    guild.getOwner().getDefaultChannel().sendMessage("There are too many roles named '" + name + "' in your server, there should be at most 1.").queue();
                }
                else {
                    RoleAction roleAction = guild.getController().createRole();
                    roleAction.setName(name);
                    roleAction.setColor(color);
                    Role role = roleAction.complete();

                    for(TextChannel textChannel : guild.getTextChannels()) {
                        PermissionOverride permissionOverride = textChannel.getPermissionOverride(role);

                        System.out.println(textChannel + ", " + permissionOverride);
                        if(permissionOverride == null) {
                            permissionOverride = textChannel.createPermissionOverride(role).complete();
                        }

                        for(Permission value : permissions.keySet()) {
                            if(permissions.get(value)) {
                                permissionOverride.getManager().grant(value).queue();
                            }
                            else {
                                permissionOverride.getManager().deny(value).queue();
                            }
                        }
                    }

                    this.roleMap.put(guild, role);
                }
            }
            catch (ErrorResponseException e) {
                if(e.getErrorCode() != 30005) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean hasRank(Rank rank) {
        if(rank == null) return false;
        return this.ordinal() >= rank.ordinal();
    }

    public boolean hasRole(Member member) {
        for(Role role : member.getRoles()) {
            if(role.getId().equals(getRole(member.getGuild()).getId())) {
                return true;
            }
        }
        return false;
    }

    public static Rank getRank(Member member) {
        if(member == null) return MEMBER;
        if(member.getRoles() == null) return MEMBER;
        if(member.getRoles().size() < 1) return MEMBER;

        Rank outRank = MEMBER;

        for(Role role : member.getRoles()) {
            for(Rank rank : values()) {
                if(role == null) continue;
                if(rank == null) continue;
                if(!rank.roleMap.containsKey(member.getGuild())) continue;

                if(role.getId().equals(rank.roleMap.get(member.getGuild()).getId())) {
                    if(outRank == MEMBER) {
                        outRank = rank;
                    }
                    outRank = outRank.hasRank(rank) ? outRank : rank;
                }
            }
        }
        return outRank;
    }

    public boolean doInitialize() {
        return initialize;
    }

    public Role getRole(Guild guild) {
        return roleMap.get(guild);
    }
}
