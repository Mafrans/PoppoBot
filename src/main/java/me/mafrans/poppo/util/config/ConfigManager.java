package me.mafrans.poppo.util.config;

import me.mafrans.poppo.util.FileUtils;
import me.mafrans.poppo.util.StringFormatter;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Properties;

public enum ConfigManager {
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
    DATABASE_TABLE("database-table", "userlist"),
    DATABASE_USERNAME("database-username", "username"),
    DATABASE_PASSWORD("database-password", "password"),
    HTTPD_URL("httpd-url", "http://localhost:8081"),
    HTTPD_PORT("httpd-port", "8081");

    private String key;
    private String defaultEntry;
    private static Yaml yaml;
    private static ConfigObject config;
    private static Properties properties = new Properties();

    ConfigManager(String key, String defaultEntry) {
        this.key = key;
        this.defaultEntry = defaultEntry;
    }

    public String getDefaultEntry() {
        return defaultEntry;
    }

    public String getKey() {
        return key;
    }

    public static ConfigObject load() throws IOException {
        File file = new File("config.yml");
        yaml = new Yaml();
        if(!file.exists()) {
            saveDefaults();
        }

        System.out.println(IOUtils.toString(new FileReader(file)));
        config = yaml.loadAs(IOUtils.toString(new FileReader(file)), ConfigObject.class);
        //properties.load(new FileReader(file));
        return config;
    }

    public static void saveDefaults() throws IOException {
        FileUtils.createResource("config.yml", new File("config.yml"));
    }

    public static void saveConfig() {
        //if(propertiesFile == null) System.out.println("Something went wrong when setting the config value, please contact a Poppo developer!");
        try {
            FileWriter fileWriter = new FileWriter(new File("config.yml"));
            IOUtils.write(yaml.dump(config), fileWriter);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}