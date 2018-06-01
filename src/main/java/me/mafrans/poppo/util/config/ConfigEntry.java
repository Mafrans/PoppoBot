package me.mafrans.poppo.util.config;

import me.mafrans.poppo.util.StringFormatter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public enum ConfigEntry {
    COMMAND_PREFIX("command-prefix", "poppo "),
    TOKEN("token", "aaaabbbbccccdddd"),
    CLIENT_ID("client-id", "12345678987654321"),
    CLIENT_SECRET("client-secret", StringFormatter.getRandom(32)),
    GOOGLE_TOKEN("google-token", "eeeeffffgggghhhh"),
    PEARSON_TOKEN("pearson-token", "iiiijjjjkkkkllll"),
    TWITCH_TOKEN("twitch-token", "mmmmnnnnoooopppp"),
    AUTOINSULT_USE("autoinsult-use", "false"),
    AUTOINSULT_RATE("autoinsult-rate", "100"),
    DEBUG_USERS("debug-users", ""),
    OVERLORD_USERS("overlord-users", ""),
    HTTPD_URL("httpd-url", "http://localhost:8081");

    private String key;
    private String defaultEntry;
    private static Properties properties = new Properties();
    private File propertiesFile = null;

    ConfigEntry(String key, String defaultEntry) {
        this.key = key;
        this.defaultEntry = defaultEntry;
    }

    public String getDefaultEntry() {
        return defaultEntry;
    }

    public String getKey() {
        return key;
    }

    public static Properties load(File file) throws IOException {
        if(!file.exists()) {
            file.createNewFile();
            saveDefaults(file);
        }

        properties.load(new FileReader(file));
        return properties;
    }

    public static void saveDefaults(File file) throws IOException {

        for(ConfigEntry entry : ConfigEntry.values()) {
            properties.setProperty(entry.getKey(), entry.getDefaultEntry());
        }

        properties.store(new FileWriter(file), "Configuration for Poppo");
    }

    public String getString() {
        return (String) properties.getProperty(getKey());
    }

    public int getInteger() {
        return Integer.parseInt(getString());
    }

    public double getDouble() {
        return Double.parseDouble(getString());
    }

    public float getFloat() {
        return Float.parseFloat(getString());
    }

    public boolean getBoolean() { return Boolean.parseBoolean(getString()); }

    public void set(String value) {
        if(propertiesFile == null) System.out.println("Something went wrong when setting the config value, please contact a Poppo developer!");
        try {
            properties.setProperty(key, value);
            properties.store(new FileWriter(propertiesFile), "Configuration for Poppo");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}