package me.mafrans.poppo.util.timedtasks;

import me.mafrans.poppo.Main;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.RichPresence;
import org.json.JSONObject;

import java.util.Date;
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
                game = Game.playing("100% Orange Juice");
                break;
            case 1:
                game = Game.playing("with " + characters[random.nextInt(characters.length)] + "!");
                break;
        }

        if(game != null) {
            Main.jda.getPresence().setGame(game);
        }
    }
}
