package me.mafrans.poppo.util.web;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.objects.Definition;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefinitionGetter {
    public static List<Definition> getHits(String query) throws IOException {
        if(getUrbanHits(query).isEmpty()) {
            return getPearsonHits(query);
        }
        else return getUrbanHits(query);
    }

    public static List<Definition> getPearsonHits(String query) throws IOException {
        List<Definition> definitionList = new ArrayList<>();
        HashMap<String, String> preferences = new HashMap<>();
        preferences.put("headword", query.toLowerCase());
        preferences.put("apikey", Main.config.pearson_token);

        JSONObject json = HTTPUtil.getJSON("https://api.pearson.com/v2/dictionaries/ldoce5/entries", preferences);

        for(int i = 0; i < json.getJSONArray("results").length()-1; i++) {
            if(!json.getJSONArray("results").getJSONObject(i).getString("headword").equalsIgnoreCase(query)) {
                definitionList.add(Definition.parsePearsonFromJSON(json.getJSONArray("results").getJSONObject(i)));
            }
        }

        return definitionList;
    }

    public static List<Definition> getUrbanHits(String query) throws IOException {
        List<Definition> definitionList = new ArrayList<>();
        HashMap<String, String> preferences = new HashMap<>();
        preferences.put("term", query.toLowerCase());

        JSONObject json = HTTPUtil.getJSON("http://api.urbandictionary.com/v0/define", preferences);

        if(json.getJSONArray("list").length() - 1 < 1) {
            return null;
        }

        for(int i = 0; i < json.getJSONArray("list").length()-1; i++) {
            definitionList.add(Definition.parseUrbanFromJSON(json.getJSONArray("list").getJSONObject(i)));
        }

        return definitionList;
    }
}
