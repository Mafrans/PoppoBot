package me.mafrans.poppo.util.timedtasks;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.config.ServerPrefs;
import me.mafrans.poppo.util.web.HTTPUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitchEvents {
    public static Map<Guild, List<String>> runningStreams = new HashMap<>();

    public static TwitchEventRunner getRunner() {
        return new TwitchEventRunner();
    }

    public static void onStreamStart(String streamName) {
        for(Guild guild : ServerPrefs.serverPrefList.keySet()) {
            if(guild == null) continue;
            String msgChannel = ServerPrefs.TWITCH_MESSAGE_CHANNEL.getString(guild); //getString(Guild guild) contains null checking
            System.out.println("MsgChannel: " + msgChannel);
            if(msgChannel == null) break;

            TextChannel textChannel = Main.jda.getTextChannelById(msgChannel);
            System.out.println("TextChannel: " + textChannel.getName());
            textChannel.sendMessage(ServerPrefs.TWITCH_START_MESSAGE.getString(guild).replace("%name", streamName).replace("%url", "https://twitch.tv/" + streamName).replace("\\n","\n")).complete();

            JSONObject stream = null;
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("client_id", Main.config.twitch_token);
                stream = HTTPUtil.getJSON("https://api.twitch.tv/kraken/streams/" + streamName.toLowerCase(), params).getJSONObject("stream");
                System.out.println(stream.toString());
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if(stream != null) {

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setImage(stream.getJSONObject("preview").getString("large"));
                embedBuilder.setAuthor(stream.getJSONObject("channel").getString("display_name"), stream.getJSONObject("channel").getString("url"), stream.getJSONObject("channel").getString("logo"));
                embedBuilder.setTitle(stream.getJSONObject("channel").getString("status"), stream.getJSONObject("channel").getString("url"));
                embedBuilder.addField("Game", stream.getString("game"), true);
                embedBuilder.addField("Viewers", String.valueOf(stream.getInt("viewers")), true);
                embedBuilder.setColor(new Color(100, 65, 164));

                textChannel.sendMessage(embedBuilder.build()).queue();
            }
        }
    }

    public static void onStreamStop(String streamName) {
        for(Guild guild : ServerPrefs.serverPrefList.keySet()) {
            if(guild == null) continue;
            String msgChannel = ServerPrefs.TWITCH_MESSAGE_CHANNEL.getString(guild); //getString(Guild guild) contains null checking
            System.out.println("MsgChannel: " + msgChannel);
            if(msgChannel == null) break;

            TextChannel textChannel = Main.jda.getTextChannelById(msgChannel);
            System.out.println("TextChannel: " + msgChannel);
            textChannel.sendMessage(ServerPrefs.TWITCH_STOP_MESSAGE.getString(guild).replace("%name", streamName).replace("%url", "https://twitch.tv/" + streamName).replace("\\n","\n")).queue();
        }
    }
}
