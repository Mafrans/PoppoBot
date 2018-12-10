package me.mafrans.poppo.util.timedtasks;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.config.ConfigManager;
import me.mafrans.poppo.util.config.ServerPrefs;
import me.mafrans.poppo.util.web.HTTPUtil;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TwitchEventRunner implements Runnable {
    @Override
    public void run() {
        System.out.println(ServerPrefs.serverPrefList.keySet());
        for(Guild guild : ServerPrefs.serverPrefList.keySet()) {
            if(guild == null) continue;

            if(ServerPrefs.TWITCH_LINKS.getString(guild) == null) continue;
            String[] twitchLinks = ServerPrefs.TWITCH_LINKS.getString(guild).toLowerCase().split(",");
            if(twitchLinks.length == 0) break;

            for(String twitchLink : twitchLinks) {
                boolean streamRunning = false;
                try {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("client_id", Main.config.twitch_token);
                    streamRunning = HTTPUtil.getJSON("https://api.twitch.tv/kraken/streams/" + twitchLink.toLowerCase(), params).get("stream") instanceof JSONObject;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(twitchLink + ": " + streamRunning);

                if (TwitchEvents.runningStreams.containsKey(guild) && TwitchEvents.runningStreams.get(guild).contains(twitchLink)) {
                    if (!streamRunning) {
                        System.out.println("Stream end");
                        List<String> streams = TwitchEvents.runningStreams.get(guild);
                        streams.remove(twitchLink);
                        TwitchEvents.runningStreams.put(guild, streams);
                        TwitchEvents.onStreamStop(twitchLink);
                    }
                    else {
                        System.out.println("Already not running");
                    }
                }
                else {
                    if (streamRunning) {
                        System.out.println("Stream Start");
                        List<String> streams = new ArrayList<>();
                        if(TwitchEvents.runningStreams.containsKey(guild)) {
                            streams = TwitchEvents.runningStreams.get(guild);
                        }
                        streams.add(twitchLink);
                        TwitchEvents.runningStreams.put(guild, streams);
                        TwitchEvents.onStreamStart(twitchLink);
                    }
                    else {
                        System.out.println("Already running");
                    }
                }
            }
        }
    }
}