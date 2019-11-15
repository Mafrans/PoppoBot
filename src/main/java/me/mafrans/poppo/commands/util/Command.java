package me.mafrans.poppo.commands.util;

import lombok.Data;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/**
 * <p>
 * A Utility class that contains the necessary
 * information to correctly execute a command.
 * </p>
 * Not to be confused with {@link me.mafrans.poppo.commands.util.ICommand}.
 *
 * @author Mafrans
 * @version 1.0
 * @since 1.0
 */
@Data
public class Command {
    /**
     * Simple command name string.
     */
    private String cmd;

    /**
     * Arguments that can be used in the command.
     * <p>
     * Commands are parsed as:
     * {@code <prefix><label> <args[0]> <args[1]> <args[...]>}
     * </p>
     */
    private String[] args;

    /**
     * The user that executed the command.
     */
    private User author;

    /**
     * The executor object that handles running the command.
     */
    private ICommand executor;

    /**
     * The Discord message that originally activated the command.
     */
    private Message message;

    /**
     * The label used to activate the command.
     * <p>
     * Not to be mistaken with {@link #cmd},
     * as it does not resemble any alias used.
     * </p>
     */
    private String label;

    /**
     * Whether to override any permission checks for the command.
     * Can only be used by Overlord users.
     */
    private boolean override;

    /**
     * Create new command object.
     *
     * @param cmd {@link #cmd}
     * @param args {@link #args}
     * @param author {@link #author}
     * @param executor {@link #executor}
     * @param message {@link #message}
     * @param label {@link #label}
     */
    public Command(final String cmd,
                   final String[] args,
                   final User author,
                   final ICommand executor,
                   final Message message,
                   final String label) {

        this.cmd = cmd;
        this.args = args;
        this.author = author;
        this.executor = executor;
        this.message = message;
        this.label = label;
        this.override = false;
    }

    /**
     * Empty command which can be edited manually.
     */
    public Command() {}
}
