package me.mafrans.poppo.commands.util;

/**
 * Categories to organize commands under.
 *
 * @author Mafrans
 * @since 1.3
 * @version 1.4
 */
public enum CommandCategory {

    /**
     * Utility commands, used mainly for productivity.
     */
    UTILITY(
        "Utility",
        ":floppy_disk:",
        "https://twemoji.maxcdn.com/svg/1f4be.svg"
    ),

    /**
     * Web commands, to some degree involved with web-api services.
     */
    WEB(
        "Web",
        ":satellite:",
        "https://twemoji.maxcdn.com/svg/1f4e1.svg"
    ),

    /**
     * Moderation commands, used to deal with clowns.
     */
    MODERATION(
        "Moderation",
        ":raised_hand:",
        "https://twemoji.maxcdn.com/svg/1f64b.svg"
    ),

    /**
     * Fun commands, primarily intended for entertainment.
     */
    FUN(
        "Fun",
        ":game_die:",
        "https://twemoji.maxcdn.com/svg/1f3b2.svg"
    ),

    /**
     * NSFW commands, commands which to some degree
     * involve nudity or otherwise lewd, pornographic or unsafe content.
     */
    NSFW(
        "NSFW",
        ":eggplant:",
        "https://twemoji.maxcdn.com/svg/1f346.svg"
    );


    /**
     * The name of the category, as displayed in the help command.
     */
    private String name;

    /**
     * The emote used to easily identify what the category includes.
     */
    private String emote;

    /**
     * An image representation of the category emote.
     */
    private String iconUrl;

    /**
     * Construct a Category via Enumerables.
     *
     * @param name {@link #name}
     * @param emote {@link #emote}
     * @param iconUrl {@link #iconUrl}
     */
    CommandCategory(final String name,
                    final String emote,
                    final String iconUrl) {
        this.name = name;
        this.emote = emote;
        this.iconUrl = iconUrl;
    }

    /**
     * The name of the category.
     *
     * @return Category Name
     */
    public String getName() {
        return name;
    }

    /**
     * The discord Emote tag used to represent the category.
     *
     * @return Emote Tag
     */
    public String getEmote() {
        return emote;
    }

    /**
     * The URL pointing towards a usable image of the category Emote.
     * May become deprecated in the future as a more stable system is designed.
     *
     * @return Icon Image URL
     */
    public String getIconUrl() {
        return iconUrl;
    }
}
