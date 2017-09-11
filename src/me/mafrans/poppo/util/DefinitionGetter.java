package me.mafrans.poppo.util;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
        String jsonText = HtmlUtil.getRawText("https://api.pearson.com/v2/dictionaries/ldoce5/entries?headword=" + query.toLowerCase() + "&apikey=" + ConfigEntry.PEARSON_TOKEN.getString());
        JSONObject json = new JSONObject(jsonText);

        for(int i = 0; i < json.getJSONArray("results").length()-1; i++) {
            definitionList.add(Definition.parsePearsonFromJSON(json.getJSONArray("results").getJSONObject(i)));
        }

        return definitionList;
    }

    public static List<Definition> getUrbanHits(String query) throws IOException {
        List<Definition> definitionList = new ArrayList<>();
        String jsonText = HtmlUtil.getRawText("http://api.urbandictionary.com/v0/define?term=" + query);
        JSONObject json = new JSONObject(jsonText);


        if(json.getJSONArray("list").length() - 1 < 1) {
            return null;
        }

        for(int i = 0; i < json.getJSONArray("list").length()-1; i++) {
            definitionList.add(Definition.parseUrbanFromJSON(json.getJSONArray("list").getJSONObject(i)));
        }

        return definitionList;
    }
}
