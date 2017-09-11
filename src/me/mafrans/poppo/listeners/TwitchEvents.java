package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.ConfigEntry;
import me.mafrans.poppo.util.HtmlUtil;
import me.mafrans.poppo.util.ServerPrefs;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.xml.soap.Text;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TwitchEvents {
    public static List<String> runningStreams = new ArrayList<>();

    public static TwitchEventRunner getRunner() {
        return new TwitchEventRunner();
    }

    public static void onStreamStart(String streamName) {
        for(Guild guild : ServerPrefs.serverPrefList.keySet()) {
            String msgChannel = ServerPrefs.TWITCH_MESSAGE_CHANNEL.getString(guild); //getString(Guild guild) contains null checking

            if(msgChannel == null) break;

            TextChannel textChannel = Main.jda.getTextChannelById(msgChannel);
            textChannel.sendMessage(ServerPrefs.TWITCH_START_MESSAGE.getString(guild).replace("%name", ServerPrefs.TWITCH_LINK.getString(guild)).replace("%url", "https://twitch.tv/" + ServerPrefs.TWITCH_LINK.getString(guild).toLowerCase()).replace("\\n","\n")).queue();
        }
    }

    public static void onStreamStop(String streamName) {
        for(Guild guild : ServerPrefs.serverPrefList.keySet()) {
            String msgChannel = ServerPrefs.TWITCH_MESSAGE_CHANNEL.getString(guild); //getString(Guild guild) contains null checking

            if(msgChannel == null) break;

            TextChannel textChannel = Main.jda.getTextChannelById(msgChannel);
            textChannel.sendMessage(ServerPrefs.TWITCH_STOP_MESSAGE.getString(guild).replace("%name", ServerPrefs.TWITCH_LINK.getString(guild)).replace("%url", "https://twitch.tv/" + ServerPrefs.TWITCH_LINK.getString(guild).toLowerCase()).replace("\\n","\n")).queue();
        }
    }
}

class TwitchEventRunner implements Runnable {
        @Override
        public void run() {
            for(Guild guild : ServerPrefs.serverPrefList.keySet()) {
                String twitchLink = ServerPrefs.TWITCH_LINK.getString(guild).toLowerCase();
                if(twitchLink == null) break;
                boolean streamRunning = false;
                try {
                    streamRunning = !HtmlUtil.getRawText("https://api.twitch.tv/kraken/streams/" + twitchLink.toLowerCase() + "?client_id=" + ConfigEntry.TWITCH_TOKEN.getString()).startsWith("{\"stream\":null");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Stream running: " + streamRunning);

                if(TwitchEvents.runningStreams.contains(twitchLink)) {
                    if(!streamRunning) {
                        TwitchEvents.runningStreams.remove(twitchLink);
                        TwitchEvents.onStreamStop(twitchLink);
                    }
                }
                else {
                    if(streamRunning) {
                        TwitchEvents.runningStreams.add(twitchLink);
                        TwitchEvents.onStreamStart(twitchLink);
                    }
                }
            }
        }
    }
