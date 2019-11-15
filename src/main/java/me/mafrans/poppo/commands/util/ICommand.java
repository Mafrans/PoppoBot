package me.mafrans.poppo.commands.util;

import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Interface for creating Command Executors.
 *
 * @author Mafrans
 * @since 1.0
 * @version 1.0
 */
public interface ICommand {
    /**
     * <p>
     * The command name.
     * Should be lower case, only contain
     * alphanumerical characters and not contain any spaces.
     * </p>
     *
     * @return The command name.
     */
    String getName();

    /**
     * Command metadata describing the command's properties.
     * @return Command Metadata.
     */
    CommandMeta getMeta();

    /**
     * The event which fires as the command is activated.
     * Put all your command code here.
     *
     * @param command The command which was activated.
     * @param channel The channel in which the command was activated.
     * @return {@literal false} if the command usage was not correct.
     * @throws Exception Command exceptions are handled outside the executable.
     */
    boolean onCommand(Command command, TextChannel channel) throws Exception;
}
