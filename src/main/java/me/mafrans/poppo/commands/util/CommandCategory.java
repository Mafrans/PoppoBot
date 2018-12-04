package me.mafrans.poppo.commands.util;

public enum CommandCategory {
    UTILITY("Utility", ":floppy_disk:", "https://twemoji.maxcdn.com/svg/1f4be.svg"),
    WEB("Web", ":satellite:", "https://twemoji.maxcdn.com/svg/1f4e1.svg"),
    MODERATION("Moderation", ":raised_hand:", "https://twemoji.maxcdn.com/svg/1f64b.svg"),
    FUN("Fun", ":game_die:", "https://twemoji.maxcdn.com/svg/1f3b2.svg"),
    ;

    private String name;
    private String emote;
    private String iconUrl;
    CommandCategory(String name, String emote, String iconUrl) {
        this.name = name;
        this.emote = emote;
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public String getEmote() {
        return emote;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
