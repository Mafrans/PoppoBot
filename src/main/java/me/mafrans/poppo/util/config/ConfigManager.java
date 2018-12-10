package me.mafrans.poppo.util.config;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.FileUtils;
import me.mafrans.poppo.util.StringFormatter;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
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
        FileUtils.createResource("config.yml", new File("config.yml"), Main.class);
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