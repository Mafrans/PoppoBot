package me.mafrans.poppo.util.web;

import me.mafrans.poppo.util.config.ConfigEntry;
import me.mafrans.poppo.util.objects.Definition;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefinitionGetter {
    public static List<Definition> getHits(String query) throws IOException {
        if(getPearsonHits(query).isEmpty()) {
            return getUrbanHits(query);
        }
        else return getPearsonHits(query);
    }

    public static List<Definition> getPearsonHits(String query) throws IOException {
        List<Definition> definitionList = new ArrayList<>();
        HashMap<String, String> preferences = new HashMap<>();
        preferences.put("headword", query.toLowerCase());
        preferences.put("apikey", ConfigEntry.PEARSON_TOKEN.getString());

        JSONObject json = HtmlUtil.getJSON("https://api.pearson.com/v2/dictionaries/ldoce5/entries", preferences);

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

        JSONObject json = HtmlUtil.getJSON("http://api.urbandictionary.com/v0/define", preferences);

        if(json.getJSONArray("list").length() - 1 < 1) {
            return null;
        }

        for(int i = 0; i < json.getJSONArray("list").length()-1; i++) {
            definitionList.add(Definition.parseUrbanFromJSON(json.getJSONArray("list").getJSONObject(i)));
        }

        return definitionList;
    }
}
