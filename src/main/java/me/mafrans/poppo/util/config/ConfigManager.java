package me.mafrans.poppo.util.config;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.FileUtils;
import me.mafrans.poppo.util.StringFormatter;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static Yaml yaml;
    private static ConfigObject config;

    public static ConfigObject load() throws IOException {
        File file = new File("config.yml");
        yaml = new Yaml();
        if(!file.exists()) {
            saveDefaults();
        }

        System.out.println(IOUtils.toString(new FileReader(file)));
        config = yaml.loadAs(IOUtils.toString(new FileReader(file)), ConfigObject.class);
        return config;
    }

    public static void saveDefaults() throws IOException {
        FileUtils.createResource("config.yml", new File("config.yml"), Main.class);
    }

    public static void saveConfig() {
        try {
            FileWriter fileWriter = new FileWriter(new File("config.yml"));
            IOUtils.write(yaml.dump(config), fileWriter);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}