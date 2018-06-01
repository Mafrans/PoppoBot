package me.mafrans.poppo.util.config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataUser {
    private List<String> names;
    private String uuid;
    private String lastOnlineTag;
    private String avatarUrl;

    public DataUser(List<String> names, String uuid, String lastOnlineTag, String avatarUrl) {
        this.names = names;
        this.uuid = uuid;
        this.lastOnlineTag = lastOnlineTag;
        this.avatarUrl = avatarUrl;
    }

    public String getLastOnlineTag() {
        return lastOnlineTag;
    }

    public String getAvatarURL() {
        return avatarUrl;
    }

    public String getUuid() {
        return uuid;
    }

    public List<String> getNames() {
        return names;
    }

    public static DataUser parse(JSONObject userjson) {
        List<String> names = new ArrayList<>();
        for(int i = 0; i < userjson.getJSONArray("names").length(); i++) {
            names.add(userjson.getJSONArray("names").getString(i));
        }

        return new DataUser(names, userjson.getString("uuid"), userjson.getString("lastonline"), userjson.getString("avatar"));
    }

    public JSONObject encode() {
        JSONObject json = new JSONObject();

        JSONArray nameArray = new JSONArray();
        for(String name : names) {
            nameArray.put(name);
        }
        json.put("names", nameArray);
        json.put("uuid", uuid);
        json.put("lastonline", lastOnlineTag);
        json.put("avatar", avatarUrl);

        return json;
    }

    public void setLastOnlineTag(String lastOnlineTag) {
        this.lastOnlineTag = lastOnlineTag;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
