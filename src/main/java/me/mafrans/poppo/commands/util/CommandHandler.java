package me.mafrans.poppo.commands.util;

import lombok.Getter;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.FeatureManager;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.mafrans.poppo.Main.config;

public class CommandHandler {
    @Getter private static List<ICommand> commandList = new ArrayList<>();

    public static Command parseCommand(Message message) {
        String content = message.getContentRaw();
        if(!content.toLowerCase().startsWith(config.command_prefix.toLowerCase())) {
            return null;
        }

        String[] words = content.substring(config.command_prefix.length()).split(" ");

        System.out.println(Arrays.toString(words));

        Command outCommand = new Command();
        outCommand.setAuthor(message.getAuthor());
        outCommand.setMessage(message);

        if(Main.config.overlord_users.contains(message.getAuthor().getId()) && (words[words.length-1].equalsIgnoreCase("please")||words[words.length-1].equalsIgnoreCase("pls"))) {
            outCommand.setArgs(ArrayUtils.subarray(words, 1, words.length - 1));
            outCommand.setOverride(true);
        }
        else {
            outCommand.setArgs(ArrayUtils.subarray(words, 1, words.length));
        }

        for(ICommand cmd : commandList) {
            String name = cmd.getName().toLowerCase();
            CommandMeta meta = cmd.getMeta();

            if(name.equalsIgnoreCase(words[0]) || (meta.getAliases() != null && ArrayUtils.contains(meta.getAliases(), words[0].toLowerCase()))) {
                try {
                    FeatureManager fm = new FeatureManager(message.getGuild());
                    if(!fm.isEnabled("command:" + cmd.getName())) {
                        System.out.println("command " + cmd.getName() + " is disabled");
                        return null;
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }


                outCommand.setLabel(words[0]);
                outCommand.setCmd(name);
                outCommand.setExecutor(cmd);
                break;
            }
        }

        if(outCommand.getExecutor() == null) return null;

        return outCommand;
    }

    public static ICommand getCommand(String name) {
        for(ICommand command : commandList) {
            if(command.getName().equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }

    public static void registerFeatures() {
        for(ICommand command : commandList) {
            FeatureManager.register("command:" + command.getName());
        }
    }

    public static void addCommand(ICommand command) {
        commandList.add(command);
    }
}
