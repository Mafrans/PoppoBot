package me.mafrans.poppo.util;

import org.json.JSONObject;

public class Information {
    private String title;
    private String description;
    private String detailedDescription;
    private String url;
    private String readMoreUrl;

    public Information(String title, String description, String detailedDescription, String url, String readMoreUrl) {
        this.title = title;
        this.description = description;
        this.detailedDescription = detailedDescription;
        this.url = url;
        this.readMoreUrl = readMoreUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public String getReadMoreUrl() {
        return readMoreUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public void setReadMoreUrl(String readMoreUrl) {
        this.readMoreUrl = readMoreUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public static Information parseFromJSON(JSONObject result) {
        String title = null;
        String description = null;
        String detailedDescription = null;
        String url = null;
        String readMoreUrl = null;

        if(result.has("name"))
            title = result.getString("name");

        if(result.has("description"))
            description = result.getString("description");

        if(result.has("detailedDescription"))
            if(result.getJSONObject("detailedDescription").has("articleBody"))
                detailedDescription = result.getJSONObject("detailedDescription").getString("articleBody");

        if(result.has("url"))
            url = result.getString("url");

        if(result.has("detailedDescription"))
            if(result.getJSONObject("detailedDescription").has("url"))
                readMoreUrl = result.getJSONObject("detailedDescription").getString("url");

        return new Information(title, description, detailedDescription, url, readMoreUrl);
    }
}
