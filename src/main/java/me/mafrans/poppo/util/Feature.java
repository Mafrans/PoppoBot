package me.mafrans.poppo.util;

import lombok.Getter;
import me.mafrans.poppo.Main;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Feature {
    @Getter private static Map<Class<?>, Id> featureCache = new HashMap<>();

    @Getter private String id;
    @Getter private Guild guild;
    @Getter private boolean enabled;
    public Feature(Guild guild, String id) {
        initFeature(guild, id);
    }

    public Feature(Guild guild, Class clazz) {
        initFeature(guild, featureCache.get(clazz).value());
    }

    private void initFeature(Guild guild, String id) {
        this.guild = guild;
        this.id = id;

        JSONObject config = getConfig();

        enabled = true;
        if(config != null && config.getJSONObject("features").has(id)) {
            if(!config.getJSONObject("features").getBoolean(id)) {
                enabled = false;
            }
        }
    }

    public static boolean isEnabled(Guild guild, String id) {
        return new Feature(guild, id).isEnabled();
    }
    
    public static boolean isEnabled(Guild guild, Class clazz) {
        return new Feature(guild, clazz).isEnabled();
    }

    public void setEnabled(boolean enabled) {
        JSONObject config = getConfig();
        JSONObject jsonObject = config.getJSONObject("features");
        jsonObject.put(id, enabled);
        config.put("features", jsonObject);
        setConfig(config);
        System.out.println(id + " is now " + (isEnabled() ? "enabled" : "disabled"));

        this.enabled = enabled;
    }

    private JSONObject getConfig() {
        JSONObject config = null;
        try {
            config = Main.serverPrefs.getPreferences(guild);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    private void setConfig(JSONObject config) {
        try {
            Main.serverPrefs.savePreferences(guild, config);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cacheFeatures() {
        System.out.println("Caching features...");
        Set<Class<?>> classes = GUtil.getReflections("me.mafrans.poppo").getTypesAnnotatedWith(Id.class);

        for(Class clazz : classes) {
            if (clazz.getAnnotationsByType(Id.class).length > 0) {
                Id id = (Id) clazz.getAnnotation(Id.class);
                System.out.println("id: " + id);
                if(id != null) {
                    System.out.println("Cached: " + id.value());
                    featureCache.put(clazz, id);
                }
            }
        }
    }

    public static void saveDefaults() {
        if(getFeatureCache().isEmpty()) {
            cacheFeatures();
        }
        for(Guild guild : Main.jda.getGuilds()) {
            for (Class clazz : featureCache.keySet()) {
                Feature feature = new Feature(guild, clazz);
                feature.setEnabled(feature.isEnabled()); // Stupid way to do it, but works
            }
        }
    }
}
