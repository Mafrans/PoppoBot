package me.mafrans.poppo.util.timedtasks;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.web.HTTPUtil;
import net.dv8tion.jda.core.entities.Game;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class GameChangeRunner implements Runnable {

    private final String[] characters = new String[] {
            "Kai",                  "QP",           "Suguri",           "Marc",         "Yuki",
            "Aru",                  "Hime",         "Sora", "           Fernet",        "Peat",
            "Tomomo",               "Syura",        "Nanako",           "Saki",         "Kyousuke",
            "Krila",                "Kae",          "Alte",             "Kyoko",        "Sham",
            "Sherry",               "Star Breaker", "Sweet Breaker",    "Nath",         "Tomato & Mimyuu",
            "Kiriko",               "NoName",       "Miusaki",          "Ceoreparque",  "Tequila",
            "Tequila's Pirates",    "Tsih",         "Mei",              "Natsumi",      "Jonathan"
    };


    @Override
    public void run() {
        Random random = new Random();
        int rint = random.nextInt(2);
        Game game = null;

        switch (rint) {
            case 0:
                game = Game.listening("'" + Main.config.command_prefix + "'");
                break;
            case 1:
                game = Game.streaming("http://poppobot.ga/", "http://poppobot.ga/");
                break;
        }

        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("client_id", Main.config.twitch_token);
            boolean streamRunning = HTTPUtil.getJSON("https://api.twitch.tv/kraken/streams/mafrans", params).get("stream") instanceof JSONObject;
            if(streamRunning) {
                JSONObject jsonObject = HTTPUtil.getJSON("https://api.twitch.tv/kraken/streams/mafrans", params).getJSONObject("stream");

                game = Game.streaming(jsonObject.getString("game"), jsonObject.getJSONObject("channel").getString("url"));
            }
        }
        catch (IOException ignored) { }

        if(game != null) {
            Main.jda.getPresence().setGame(game);
        }
    }
}
