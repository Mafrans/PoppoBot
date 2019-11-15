package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.objects.Rank;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.IOException;

public class ServerListener extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        for(Rank rank : Rank.values()) {
            rank.initialize(event.getGuild());
        }

        try {
            Main.serverPrefs.saveDefaults(event.getGuild());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
