package me.mafrans.poppo.commands.util;

import lombok.Data;

@Data
public class CommandMeta {
    /**
     * The category this command belongs to.
     */
    private CommandCategory category;

    /**
     * A short description of the command.
     */
    private String description;

    /**
     * The usage of the command.
     * <p>
     * Where
     * {@literal <argument>} is a required argument,
     * {@literal [argument]} is an optional argument,
     * and {@literal <option1|option2>} is a choice between two options.
     * </p>
     */
    private String usage;

    /**
     * Aliases that allow multiple alternative command labels.
     */
    private String[] aliases;

    /**
     * Is the command limited to Bot Commanders?
     * <p>
     * @deprecated Use the Rank feature found at
     * {@link me.mafrans.poppo.util.objects.Rank} instead.
     * </p>
     */
    private boolean botCommanderOnly;

    /**
     * <p>
     * Should the command be hidden from
     * the Help meny found in {@link me.mafrans.poppo.commands.Command_help}?
     * </p>
     */
    private boolean hidden;

    /**
     * Command Meta without a {@literal hidden} parameter.
     *
     * @param category {@link #category}
     * @param description {@link #description}
     * @param usage {@link #usage}
     * @param aliases {@link #aliases}
     * @param botCommanderOnly {@link #botCommanderOnly}
     */
    public CommandMeta(
            final CommandCategory category,
            final String description,
            final String usage,
            final String[] aliases,
            final boolean botCommanderOnly) {
        this.category = category;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
        this.botCommanderOnly = botCommanderOnly;
    }

    /**
     * Command Meta without a {@literal botCommanderOnly} parameter.
     *
     * @param category {@link #category}
     * @param description {@link #description}
     * @param usage {@link #usage}
     * @param aliases {@link #aliases}
     */
    public CommandMeta(
            final CommandCategory category,
            final String description,
            final String usage,
            final String[] aliases) {
        this.category = category;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
    }

    /**
     * Command Meta without a {@literal botCommanderOnly} parameter.
     *
     * @param category {@link #category}
     * @param description {@link #description}
     * @param usage {@link #usage}
     * @param aliases {@link #aliases}
     * @param botCommanderOnly {@link #botCommanderOnly}
     * @param hidden {@link hidden}
     */
    public CommandMeta(
            final CommandCategory category,
            final String description,
            final String usage,
            final String[] aliases,
            final boolean botCommanderOnly,
            final boolean hidden) {
        this.category = category;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
        this.botCommanderOnly = botCommanderOnly;
        this.hidden = hidden;
    }
}
