package me.mafrans.poppo.commands.util;

import lombok.Data;

@Data
public class CommandMeta {
    private CommandCategory category;
    private String description;
    private String usage;
    private String[] aliases;
    private boolean botCommanderOnly;
    private boolean hidden;

    public CommandMeta(CommandCategory category, String description, String usage, String[] aliases, boolean botCommanderOnly) {
        this.category = category;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
        this.botCommanderOnly = botCommanderOnly;
    }

    public CommandMeta(CommandCategory category, String description, String usage, String[] aliases, boolean botCommanderOnly, boolean hidden) {
        this.category = category;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
        this.botCommanderOnly = botCommanderOnly;
        this.hidden = hidden;
    }
}
