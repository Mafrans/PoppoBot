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
        for(Guild guild : Main.serverPrefs.getGuilds()) {
            if(guild == null) continue;

            JSONObject prefs = null;
            try {
                prefs = Main.serverPrefs.getPreferences(guild);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if(prefs == null) continue;
            if(prefs.getJSONArray("twitch_links") == null || prefs.getJSONArray("twitch_links").length() == 0) continue;
            if(prefs.getJSONArray("twitch_links").length() == 0) break;

            for(int i = 0; i < prefs.getJSONArray("twitch_links").length(); i++) {

                String twitchLink = prefs.getJSONArray("twitch_links").getString(i);

                boolean streamRunning = false;
                try {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("client_id", Main.config.twitch_token);
                    System.out.println("https://api.twitch.tv/kraken/streams/" + twitchLink.toLowerCase());
                    streamRunning = HTTPUtil.getJSON("https://api.twitch.tv/kraken/streams/" + twitchLink.toLowerCase(), params).get("stream") instanceof JSONObject;
                }
                catch (Exception e) {
                    continue;
                }
                System.out.println(twitchLink + ": " + streamRunning);

                if (TwitchEvents.runningStreams.containsKey(guild) && TwitchEvents.runningStreams.get(guild).contains(twitchLink)) {
                    if (!streamRunning) {
                        System.out.println("Stream end");
                        List<String> streams = TwitchEvents.runningStreams.get(guild);
                        streams.remove(twitchLink);
                        TwitchEvents.runningStreams.put(guild, streams);
                        try {
                            TwitchEvents.onStreamStop(twitchLink);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                        try {
                            TwitchEvents.onStreamStart(twitchLink);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        System.out.println("Already running");
                    }
                }
            }
        }
    }
}