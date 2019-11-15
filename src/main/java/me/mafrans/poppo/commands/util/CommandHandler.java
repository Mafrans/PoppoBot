package me.mafrans.poppo.commands.util;

import lombok.Getter;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.mafrans.poppo.Main.config;

/**
 * A static utility class that manages and handles commands.
 *
 * @author Mafrans
 * @version 1.5
 * @since 1.0
 */
public final class CommandHandler {
    private CommandHandler() {
    }

    /**
     * Cache containing all available commands.
     */
    @Getter
    private static List<ICommand> commandList = new ArrayList<>();

    /**
     * Parse a discord message into a Command object.
     *
     * @param message The message that should be parsed.
     * @return A functioning command object, or {@literal null} if none were found
     */
    public static Command parseCommand(final Message message) {
        String content = message.getContentRaw();

        String prefix = null;
        for (String p : config.command_prefix) {
            if (content.toLowerCase().startsWith(p.toLowerCase())) {
                prefix = p;
                break;
            }
        }

        if (prefix == null) {
            return null;
        }

        String[] words = content.substring(prefix.length()).split(" ");

        System.out.println(Arrays.toString(words));

        Command outCommand = new Command();
        outCommand.setAuthor(message.getAuthor());
        outCommand.setMessage(message);

        List<String> overlords = config.overlord_users;
        if (overlords.contains(message.getAuthor().getId())
            && (words[words.length - 1].equalsIgnoreCase("please")
                || words[words.length - 1].equalsIgnoreCase("pls"))) {

            outCommand.setArgs(
                    ArrayUtils.subarray(
                            words,
                            1,
                            words.length - 1
                    )
            );
            outCommand.setOverride(true);

        } else {
            outCommand.setArgs(ArrayUtils.subarray(words, 1, words.length));
        }

        for (ICommand cmd : commandList) {
            String name = cmd.getName().toLowerCase();
            CommandMeta meta = cmd.getMeta();

            if (name.equalsIgnoreCase(words[0])
                || (meta.getAliases() != null
                    && ArrayUtils.contains(
                            meta.getAliases(),
                            words[0].toLowerCase()))) {

                outCommand.setLabel(words[0]);
                outCommand.setCmd(name);
                outCommand.setExecutor(cmd);
                break;
            }
        }

        if (outCommand.getExecutor() == null) {
            return null;
        }

        return outCommand;
    }

    /**
     * Get a command executor from it's name.
     * @param name Command name
     * @return A functioning command executor,
     * or {@literal null} if none were found.
     */
    public static ICommand getCommand(final String name) {
        for (ICommand command : commandList) {
            if (command.getName().equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Add a command to the cache.
     * @param command The command to add.
     */
    public static void addCommand(final ICommand command) {
        commandList.add(command);
    }
}
