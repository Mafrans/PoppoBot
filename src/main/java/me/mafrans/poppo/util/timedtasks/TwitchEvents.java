package me.mafrans.poppo.util.timedtasks;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.config.ConfigEntry;
import me.mafrans.poppo.util.config.ServerPrefs;
import me.mafrans.poppo.util.web.HTTPUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TwitchEvents {
    public static List<String> runningStreams = new ArrayList<>();

    public static TwitchEventRunner getRunner() {
        return new TwitchEventRunner();
    }

    public static void onStreamStart(String streamName) {
        /*for(Guild guild : ServerPrefs.serverPrefList.keySet()) {
            String msgChannel = ServerPrefs.TWITCH_MESSAGE_CHANNEL.getString(guild); //getString(Guild guild) contains null checking

            if(msgChannel == null) break;

            TextChannel textChannel = Main.jda.getTextChannelById(msgChannel);
            textChannel.sendMessage(ServerPrefs.TWITCH_START_MESSAGE.getString(guild).replace("%name", ServerPrefs.TWITCH_LINK.getString(guild)).replace("%url", "https://twitch.tv/" + ServerPrefs.TWITCH_LINK.getString(guild).toLowerCase()).replace("\\n","\n")).queue();
        }*/
    }

    public static void onStreamStop(String streamName) {
        /*for(Guild guild : ServerPrefs.serverPrefList.keySet()) {
            String msgChannel = ServerPrefs.TWITCH_MESSAGE_CHANNEL.getString(guild); //getString(Guild guild) contains null checking

            if(msgChannel == null) break;

            TextChannel textChannel = Main.jda.getTextChannelById(msgChannel);
            textChannel.sendMessage(ServerPrefs.TWITCH_STOP_MESSAGE.getString(guild).replace("%name", ServerPrefs.TWITCH_LINK.getString(guild)).replace("%url", "https://twitch.tv/" + ServerPrefs.TWITCH_LINK.getString(guild).toLowerCase()).replace("\\n","\n")).queue();
        }*/
    }
}
