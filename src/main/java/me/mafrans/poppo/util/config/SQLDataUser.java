package me.mafrans.poppo.util.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class SQLDataUser {
    private String names;
    private String uuid;
    private String lastOnlineTag;
    private String avatarUrl;
    private int stars;

    public SQLDataUser(DataUser dataUser) {
        this.names = StringUtils.join(dataUser.getNames(), "\uE081");
        this.uuid = dataUser.getUuid();
        this.lastOnlineTag = dataUser.getLastOnlineTag();
        this.avatarUrl = dataUser.getAvatarUrl();
        this.stars = dataUser.getStars();
    }

    public SQLDataUser(String names, String uuid, String lastOnlineTag, String avatarUrl, int stars) {
        this.names = names;
        this.uuid = uuid;
        this.lastOnlineTag = lastOnlineTag;
        this.avatarUrl = avatarUrl;
        this.stars = stars;
    }
}
