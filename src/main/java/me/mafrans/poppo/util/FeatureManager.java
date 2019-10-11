package me.mafrans.poppo.util;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.config.ServerPrefs;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeatureManager {
    private JSONObject featureCache;
    private Guild guild;
    private static List<String> registeredFeatures = new ArrayList<>();

    public static void register(String feature) {
        registeredFeatures.add(feature);
    }

    public FeatureManager(Guild g) throws IOException {
        guild = g;
        JSONObject prefs = Main.serverPrefs.getPreferences(g);
        featureCache = prefs.getJSONObject("features");
    }

    public void saveDefault() throws IOException {
        for(String feature : registeredFeatures) {
            if(!featureCache.has(feature)) {
                featureCache.put(feature, true);
            }
        }
        save();
    }

    public void enable(String feature) {
        featureCache.put(feature, true);
    }

    public void disable(String feature) {
        featureCache.put(feature, false);
    }

    public boolean isEnabled(String feature) {
        return featureCache.getBoolean(feature);
    }


    public void save() throws IOException {
        JSONObject prefs = Main.serverPrefs.getPreferences(guild);
        prefs.put("features", featureCache);
        Main.serverPrefs.savePreferences(guild, prefs);
    }
}
