package me.mafrans.poppo.commands.util;

import me.mafrans.poppo.util.config.ConfigEntry;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    private static List<ICommand> commandList = new ArrayList();

    public static Command parseCommand(Message message) {
        String content = message.getContent();
        if(!content.toLowerCase().startsWith(ConfigEntry.COMMAND_PREFIX.getString().toLowerCase())) {
            return null;
        }

        String[] words = content.replaceFirst(ConfigEntry.COMMAND_PREFIX.getString(), "").split(" ");

        Command outCommand = new Command();
        outCommand.setArgs(ArrayUtils.subarray(words, 1, words.length));
        outCommand.setAuthor(message.getAuthor());
        outCommand.setMessage(message);

        for(ICommand cmd : getCommands()) {
            String name = cmd.getName().toLowerCase();
            CommandMeta meta = cmd.getMeta();

            if(name.equalsIgnoreCase(words[0]) || (meta.getAliases() == null ? false : meta.getAliases().contains(words[0].toLowerCase()))) {
                outCommand.setLabel(words[0]);
                System.out.println("Found command " + cmd.getName());
                outCommand.setCmd(name);
                outCommand.setExecutor(cmd);
                break;
            }
        }

        if(outCommand.getExecutor() == null) return null;

        return outCommand;
    }

    public static List<ICommand> getCommands() {
        return commandList;
    }

    public static void addCommand(ICommand command) {
        commandList.add(command);
    }
}
