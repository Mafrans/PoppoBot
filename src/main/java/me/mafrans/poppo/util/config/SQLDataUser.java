package me.mafrans.poppo.util.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SQLDataUser {
    private String names;
    private String uuid;
    private String lastOnlineTag;
    private String avatarUrl;

    public SQLDataUser(DataUser dataUser) {
        this.names = StringUtils.join(dataUser.getNames(), "\uE081");
        this.uuid = dataUser.getUuid();
        this.lastOnlineTag = dataUser.getLastOnlineTag();
        this.avatarUrl = dataUser.getAvatarUrl();
    }

    public SQLDataUser(String names, String uuid, String lastOnlineTag, String avatarUrl) {
        this.names = names;
        this.uuid = uuid;
        this.lastOnlineTag = lastOnlineTag;
        this.avatarUrl = avatarUrl;
    }

    public String getNames() {
        return names;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLastOnlineTag() {
        return lastOnlineTag;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
