package me.mafrans.poppo.util;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class Definition {
    String title;
    String definition;
    String example;
    String type;
    String source;

    public Definition(String title, String definition, String example, String type, String source) {
        this.title = title;
        this.definition = definition;
        this.example = example;
        this.type = type;
        this.source = source;
    }

    public Definition() { }

    public String getTitle() {
        return title;
    }

    public String getSource() {
        return source;
    }

    public String getType() {
        return type;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefinition() {
        return definition;
    }

    public String getExample() {
        return example;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public static Definition parsePearsonFromJSON(JSONObject result) {
        String title = null;
        String definition = null;
        String example = null;
        String type = null;

        System.out.println(result.toString());
        if(result.has("headword"))
            title = result.getString("headword");

        type = result.has("part_of_speech") ? result.getString("part_of_speech") : null;

        if(result.has("senses"))
            if(result.getJSONArray("senses").length() > 0) {
                if(result.getJSONArray("senses").getJSONObject(0).has("definition"))
                    definition = result.getJSONArray("senses").getJSONObject(0).getJSONArray("definition").getString(0);
                if(result.getJSONArray("senses").getJSONObject(0).has("examples"))
                    if(result.getJSONArray("senses").getJSONObject(0).getJSONArray("examples").length() > 0)
                        if(result.getJSONArray("senses").getJSONObject(0).getJSONArray("examples").getJSONObject(0).has("text"))
                            example = result.getJSONArray("senses").getJSONObject(0).getJSONArray("examples").getJSONObject(0).getString("text");
        }

        return new Definition(title, definition, example, type, "Pearson Dictionaries");
    }

    public static Definition parseUrbanFromJSON(JSONObject result) {
        String title = null;
        String definition = null;
        String example = null;
        String type = null;

        if(result.has("word"))
            title = result.getString("word");

        if(result.has("definition")) {
            definition = result.getString("definition");
        }

        if(result.has("example")) {
            example = result.getString("example");
        }

        return new Definition(title, definition, example, type, "Urbandictionary");
    }
}
