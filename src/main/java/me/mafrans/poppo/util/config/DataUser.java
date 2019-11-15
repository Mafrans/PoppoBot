package me.mafrans.poppo.util.config;

import lombok.Data;
import lombok.Setter;
import me.mafrans.poppo.util.StringFormatter;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class DataUser {
    private List<String> names;
    private String uuid;
    private String lastOnlineTag;
    private String avatarUrl;
    private int stars;

    public DataUser(List<String> names, String uuid, String lastOnlineTag, String avatarUrl, int stars) {
        this.names = names;
        this.uuid = uuid;
        this.lastOnlineTag = lastOnlineTag;
        this.avatarUrl = avatarUrl;
        this.stars = stars;
    }

    public static DataUser parse(JSONObject userjson) {
        List<String> names = new ArrayList<>();
        for(int i = 0; i < userjson.getJSONArray("names").length(); i++) {
            names.add(userjson.getJSONArray("names").getString(i));
        }
        int stars = userjson.getInt("stars");

        return new DataUser(names, userjson.getString("uuid"), userjson.getString("lastonline"), userjson.getString("avatar"), stars);
    }

    public static DataUser parse(SQLDataUser sqlDataUser) {
        List<String> names = Arrays.asList(sqlDataUser.getNames().split("\uE081"));
        String uuid = sqlDataUser.getUuid();
        String lastOnlineTag = sqlDataUser.getLastOnlineTag();
        String avatarUrl = sqlDataUser.getAvatarUrl();
        int stars = sqlDataUser.getStars();

        return new DataUser(names, uuid, lastOnlineTag, avatarUrl, stars);
    }
}
