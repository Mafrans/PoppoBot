package me.mafrans.poppo.util.config;

import lombok.Data;
import lombok.Setter;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class DataUser {
    private @Setter List<String> names;
    private @Setter String uuid;
    private @Setter String lastOnlineTag;
    private @Setter String avatarUrl;

    public DataUser(List<String> names, String uuid, String lastOnlineTag, String avatarUrl) {
        this.names = names;
        this.uuid = uuid;
        this.lastOnlineTag = lastOnlineTag;
        this.avatarUrl = avatarUrl;
    }

    public static DataUser parse(JSONObject userjson) {
        List<String> names = new ArrayList<>();
        for(int i = 0; i < userjson.getJSONArray("names").length(); i++) {
            names.add(userjson.getJSONArray("names").getString(i));
        }

        return new DataUser(names, userjson.getString("uuid"), userjson.getString("lastonline"), userjson.getString("avatar"));
    }

    public static DataUser parse(SQLDataUser sqlDataUser) {
        List<String> names = Arrays.asList(sqlDataUser.getNames().split("\uE081"));
        String uuid = sqlDataUser.getUuid();
        String lastOnlineTag = sqlDataUser.getLastOnlineTag();
        String avatarUrl = sqlDataUser.getAvatarUrl();

        return new DataUser(names, uuid, lastOnlineTag, avatarUrl);
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
}