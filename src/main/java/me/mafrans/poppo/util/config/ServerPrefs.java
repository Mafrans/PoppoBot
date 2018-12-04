package me.mafrans.poppo.util.config;

import me.mafrans.poppo.Main;
import net.dv8tion.jda.core.entities.Guild;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public enum ServerPrefs {
    TWITCH_LINKS("twitch-links", "Linked Twitch usernames."),
    TWITCH_MESSAGE_CHANNEL("twitch-message-channel", "Message channel for announcements."),
    TWITCH_START_MESSAGE("twitch-start-message", "`%name` - channel name, `%url` - channel url, `\\n` - line break"),
    TWITCH_STOP_MESSAGE("twitch-stop-message", "`%name` - channel name, `%url` - channel url, `\\n` - line break");
    //USE_AUTOINSULT("use-autoinsult", "Defines usage of autoinsult, nsfw");

    public static HashMap<Guild, Properties> serverPrefList = new HashMap<>();
    private String key;
    private String info;
    ServerPrefs(String key, String info) {
        this.key = key;
        this.info = info;
    }

    public static Properties getPreferences(Guild guild) throws IOException {
        if(serverPrefList.containsKey(guild)) return serverPrefList.get(guild);

        System.out.println(guild);
        File pathFile = new File("./servers/" + guild.getId() + "/preferences.properties");
        Properties properties = new Properties();
        if(!pathFile.exists()) {
            return null;
        }
        else {
            properties.load(new FileReader(pathFile));
        }

        return properties;
    }

    public static void initPreferences() throws IOException {
        File serversPath = new File("./servers");
        if(!serversPath.exists()) serversPath.mkdirs();

        File[] fList = serversPath.listFiles();
        if(fList.length < 1) return;
        for(File f : fList) {
            if(f.isDirectory()) {
                File pref = new File(f.getPath() + "./preferences.properties");
                if(pref.exists()) {
                    Guild guild = Main.jda.getGuildById(f.getName());
                    serverPrefList.put(guild, getPreferences(guild));
                }
            }
        }
    }

    public String getString(Guild guild) {
        if(serverPrefList.containsKey(guild)) {
            Properties preferences = serverPrefList.get(guild);

            if(preferences.containsKey(key)) {
                return preferences.getProperty(key);
            }
        }
        return null;
    }

    public void setString(Guild guild, String value) throws IOException {
        Properties pref = new Properties();

        File file = new File("./servers/" + guild.getId() + "/preferences.properties");
        if(file.exists()) {
            pref = getPreferences(guild);
            pref.setProperty(key, value);
            pref.store(new FileWriter(file), "Preferences of server " + guild.getName() + "(" + guild.getId() + ")");
            return;
        }

        pref = new Properties();
        file.getParentFile().mkdirs();
        file.createNewFile();
        pref.setProperty(key, value);
        pref.store(new FileWriter(file), "Preferences of server " + guild.getName() + "(" + guild.getId() + ")");
    }

    public boolean exists(Guild guild) {
        if(serverPrefList.containsKey(guild)) {
            Properties preferences = serverPrefList.get(guild);

            if(preferences.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    public void remove(Guild guild) throws IOException {
        Properties pref = new Properties();

        File file = new File("./servers/" + guild.getId() + "/preferences.properties");
        if(file.exists()) {
            pref = getPreferences(guild);
            pref.remove(key);
            pref.store(new FileWriter(file), "Preferences of server " + guild.getName() + "(" + guild.getId() + ")");
            return;
        }

        pref = new Properties();
        file.getParentFile().mkdirs();
        file.createNewFile();
        pref.remove(key);
        pref.store(new FileWriter(file), "Preferences of server " + guild.getName() + "(" + guild.getId() + ")");
    }

    public String getKey() {
        return key;
    }

    public String getInfo() {
        return info;
    }
}
