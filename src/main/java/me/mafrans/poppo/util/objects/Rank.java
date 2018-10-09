package me.mafrans.poppo.util.objects;

import me.mafrans.poppo.Main;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.managers.RoleManager;
import net.dv8tion.jda.core.requests.restaction.RoleAction;

import java.awt.*;

public enum Rank {
    BOT_COMMANDER("Bot Commander", new Color(30, 170, 170)),
    ;



    private String name;
    private Color color;
    private Role role;

    Rank(String name, Color color) {
        this.name = name;
        this.color = color;
        this.role = null;
    }

    public void initialize() {
        for (Guild guild : Main.jda.getGuilds()) {
            try {
                System.out.println("Initializing " + this.name + " for guild " + guild.getName());
                if (guild.getRolesByName(name, false).size() == 1) {
                    this.role = guild.getRolesByName(name, false).get(0);
                } else if (guild.getRolesByName(name, false).size() > 1) {
                    guild.getOwner().getDefaultChannel().sendMessage("There are too many roles named '" + name + "' in your server, there should be at most 1.").queue();
                    this.role = null;
                } else {
                    RoleAction roleAction = guild.getController().createRole();
                    roleAction.setName(name);
                    roleAction.setColor(color);

                    Role role = roleAction.complete();
                    this.role = role;
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
            if(role.getName() == this.name) {
                return true;
            }
        }
        return false;
    }

    public static Rank getRank(Member member) {
        if(member == null) return null;
        if(member.getRoles() == null) return null;
        if(member.getRoles().size() < 1) return null;

        Rank outRank = null;

        for(Role role : member.getRoles()) {
            for(Rank rank : values()) {
                if(role.getName().equals(rank.role.getName())) {
                    if(outRank == null) {
                        outRank = rank;
                    }
                    outRank = outRank.hasRank(rank) ? outRank : rank;
                }
                else {
                }
            }
        }
        return outRank;
    }
}
