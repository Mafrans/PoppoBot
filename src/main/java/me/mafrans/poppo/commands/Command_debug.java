package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.StringFormatter;
import me.mafrans.poppo.util.TimerTasks;
import me.mafrans.poppo.util.config.ConfigEntry;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command_debug implements ICommand {
    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                "Runs debug scripts.",
                "debug <arguments>",
                null,
                false,
                true);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        if(!Arrays.asList(ConfigEntry.DEBUG_USERS.getString().split(",")).contains(command.getAuthor().getId())) {
            return true;
        }
        String[] args = command.getArgs();

        switch(args[0].toLowerCase()) {
            case "run":
                if(args.length != 2) {
                    debugSendMessageAsync(channel, "Correct usage for command " + command.getCmd() + " is: `debug run <class>`");
                    return true;
                }
                Class runClass;
                try {
                    runClass = Class.forName(args[1]);
                }
                catch (ClassNotFoundException e) {
                    debugSendMessageAsync(channel, "Could not find a class with the name of \"" + args[1] + ".java\"");
                    break;
                }
                Object instance = runClass.newInstance();
                if(instance instanceof Runnable) {
                    Runnable runnable = (Runnable) instance;
                    runnable.run();
                    debugSendMessageAsync(channel, "Successfully ran runnable class \"" + args[1] + ".java\"");
                    break;
                }
                channel.sendMessage("Class is not runnable.").queue();
                break;

            case "debuguser":
                if(!Arrays.asList(ConfigEntry.OVERLORD_USERS.getString().split(",")).contains(command.getAuthor().getId())) {
                    debugSendMessageAsync(channel, "You do not have permission to use this command.");
                    break;
                }
                if(args.length != 3) {
                    debugSendMessageAsync(channel, "Correct usage for command " + command.getCmd() + " is: `debug run <class>`");
                    break;
                }

                if(args[1].equalsIgnoreCase("add")) {
                    ConfigEntry.DEBUG_USERS.set(ConfigEntry.DEBUG_USERS.getString() + "," + args[1]);

                    debugSendMessageAsync(channel, "Added " + args[2] + " as a debug user.");
                }
                else if(args[1].equalsIgnoreCase("remove")) {
                    String[] debugusers = ConfigEntry.DEBUG_USERS.getString().split(",");
                    List<String> sl = new ArrayList<>();
                    boolean response = false;

                    for(String user : debugusers) {
                        if(!user.equalsIgnoreCase(args[2])) {
                            sl.add(user);
                        }
                        else {
                            response = true;
                        }
                    }

                    if(response) {
                        debugSendMessageAsync(channel, "Added `" + args[2] + "` as a debug user.");
                    }
                    else {
                        debugSendMessageAsync(channel, "Could not find a debug user with the id: `" + args[2] + "`");
                    }

                    ConfigEntry.DEBUG_USERS.set(StringUtils.join(sl, ","));

                    channel.sendMessage("Added " + args[1] + " as a debug user.");
                }
                else if(args[1].equalsIgnoreCase("list")) {
                    String[] debugUsers = ConfigEntry.DEBUG_USERS.getString().split(",");
                    debugSendMessageAsync(channel, StringFormatter.parseLines(new String[] {"```lua", Arrays.toString(debugUsers), "```"}));
                }
                else {
                    debugSendMessageAsync(channel, "Correct usage for command " + command.getCmd() + " is: `debug add|remove|list [userid]`");
                }
                break;

            case "announce":
                if(args.length < 2) {
                    debugSendMessageAsync(channel, "Correct usage for command " + command.getCmd() + " is: `debug announce <announcement>`");
                    break;
                }

                for(Guild guild : Main.jda.getGuilds()) {
                    if(args[1].equalsIgnoreCase("-rm")) {
                        if(!NumberUtils.isDigits(args[2])) {
                            channel.sendMessage("Argument -rm requires a value: <time>");
                            break;
                        }

                        int time = Integer.parseInt(args[2]);
                        final Message message = guild.getDefaultChannel().sendMessage(StringUtils.join(ArrayUtils.subarray(args, 3, args.length))).complete();

                        TimerTasks.queueTask(new Runnable() {
                            @Override
                            public void run() {
                                message.delete();
                            }
                        }, time);
                        break;
                    }

                    final Message message = guild.getDefaultChannel().sendMessage(StringUtils.join(ArrayUtils.subarray(args, 1, args.length))).complete();

                    break;
                }
        }
        return true;
    }

    public void debugSendMessageAsync(TextChannel channel, String message) {
        channel.sendMessage("```lua\n" + message + "\n```").queue();
    }

    public Message debugSendMessageBlock(TextChannel channel, String message) {
        return channel.sendMessage("```lua\n" + message + "\n```").complete();
    }
}
