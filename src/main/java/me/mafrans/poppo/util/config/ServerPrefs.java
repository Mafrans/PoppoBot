package me.mafrans.poppo.util.config;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.FileUtils;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class ServerPrefs {
    private File savePath;

    public ServerPrefs(File savePath) {
        this.savePath = savePath;
    }

    public void saveDefaults(Guild guild) throws IOException {
        File file = new File(savePath, guild.getId() + "/preferences.json");
        if(!file.exists()) {
            FileUtils.createResource("preferences.json", file, Main.class);
        }
    }

    public JSONObject getPreferences(Guild guild) throws IOException {
        File file = new File(savePath, guild.getId() + "/preferences.json");
        if(file.exists()) {
            return new JSONObject(FileUtils.readFile(file));
        }
        return new JSONObject();
    }

    public void savePreferences(Guild guild, JSONObject jsonObject) throws IOException {
        File file = new File(savePath, guild.getId() + "/preferences.json");
        FileUtils.writeFile(file, jsonObject.toString());
    }

    public List<Guild> getGuilds() {
        List<Guild> guilds = new ArrayList<>();
        for(File file : savePath.listFiles()) {
            guilds.add(Main.jda.getGuildById(file.getName()));
        }
        return guilds;
    }
}
