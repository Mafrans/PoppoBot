package me.mafrans.poppo.util.timedtasks;

import me.mafrans.poppo.util.config.ConfigEntry;
import me.mafrans.poppo.util.config.ServerPrefs;
import me.mafrans.poppo.util.web.HTTPUtil;
import net.dv8tion.jda.core.entities.Guild;

import java.io.IOException;
import java.util.HashMap;

public class TwitchEventRunner implements Runnable {
    @Override
    public void run() {
        /*for(Guild guild : ServerPrefs.serverPrefList.keySet()) {
            String twitchLink = ServerPrefs.TWITCH_LINK.getString(guild).toLowerCase();
            if(twitchLink == null) break;
            boolean streamRunning = false;
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("client_id", ConfigEntry.TWITCH_TOKEN.getString());
                streamRunning = !(HTTPUtil.getJSON("https://api.twitch.tv/kraken/streams/" + twitchLink.toLowerCase(), params).get("stream") instanceof String);
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
        */
    }
}