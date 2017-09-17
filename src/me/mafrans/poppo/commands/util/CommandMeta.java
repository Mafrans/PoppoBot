package me.mafrans.poppo.commands.util;

import java.util.List;

public class CommandMeta {
    private String description;
    private String usage;
    private List<String> aliases;
    private boolean botCommanderOnly;
    private boolean hidden;

    public CommandMeta(String description, String usage, List<String> aliases, boolean botCommanderOnly) {
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
        this.botCommanderOnly = botCommanderOnly;
    }

    public CommandMeta(String description, String usage, List<String> aliases, boolean botCommanderOnly, boolean hidden) {
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
        this.botCommanderOnly = botCommanderOnly;
        this.hidden = hidden;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean isBotCommanderOnly() {
        return botCommanderOnly;
    }

    public boolean isHidden() {
        return hidden;
    }
}
