package me.mafrans.poppo.commands.util;

import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.mafrans.poppo.Main.config;

public class CommandHandler {
    private static List<ICommand> commandList = new ArrayList();

    public static Command parseCommand(Message message) {
        String content = message.getContent();
        if(!content.toLowerCase().startsWith(config.command_prefix.toLowerCase())) {
            System.out.println("Not Command: " + message.getId());
            return null;
        }

        String[] words = content.substring(config.command_prefix.length()).split(" ");

        System.out.println(Arrays.toString(words));

        Command outCommand = new Command();
        outCommand.setAuthor(message.getAuthor());
        outCommand.setMessage(message);

        if(ConfigEntry.OVERLORD_USERS.getString().contains(message.getAuthor().getId()) && (words[words.length-1].equalsIgnoreCase("please")||words[words.length-1].equalsIgnoreCase("pls"))) {
            outCommand.setArgs(ArrayUtils.subarray(words, 1, words.length - 1));
            outCommand.setOverride(true);
        }
        else {
            outCommand.setArgs(ArrayUtils.subarray(words, 1, words.length));
        }

        for(ICommand cmd : getCommands()) {
            String name = cmd.getName().toLowerCase();
            CommandMeta meta = cmd.getMeta();

            if(name.equalsIgnoreCase(words[0]) || (meta.getAliases() != null && meta.getAliases().contains(words[0].toLowerCase()))) {
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
