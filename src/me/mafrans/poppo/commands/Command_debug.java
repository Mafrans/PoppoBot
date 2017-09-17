package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.StringFormatter;
import me.mafrans.poppo.util.config.ConfigEntry;
import me.mafrans.poppo.util.TimerTasks;
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
                    channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `debug run <class>`").queue();
                    return true;
                }
                Class runClass;
                try {
                    runClass = Class.forName(args[1]);
                }
                catch (ClassNotFoundException e) {
                    channel.sendMessage("Could not find a class with the name of \"" + args[1] + ".java\"").queue();
                    break;
                }
                Object instance = runClass.newInstance();
                if(instance instanceof Runnable) {
                    Runnable runnable = (Runnable) instance;
                    runnable.run();
                    channel.sendMessage("Successfully ran runnable class \"" + args[1] + ".java \"").queue();
                    break;
                }
                channel.sendMessage("Class is not runnable.").queue();
                break;

            case "debuguser":
                if(!Arrays.asList(ConfigEntry.OVERLORD_USERS.getString().split(",")).contains(command.getAuthor().getId())) {
                    channel.sendMessage("You do not have permission to use this command.").queue();
                    break;
                }
                if(args.length != 3) {
                    channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `debug run <class>`").queue();
                    break;
                }

                if(args[1].equalsIgnoreCase("add")) {
                    ConfigEntry.DEBUG_USERS.set(ConfigEntry.DEBUG_USERS.getString() + "," + args[1]);

                    channel.sendMessage("Added " + args[2] + " as a debug user.");
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
                        channel.sendMessage("Added `" + args[2] + "` as a debug user.").queue();
                    }
                    else {
                        channel.sendMessage("Could not find a debug user with the id: `" + args[2] + "`").queue();
                    }

                    ConfigEntry.DEBUG_USERS.set(StringUtils.join(sl, ","));

                    channel.sendMessage("Added " + args[1] + " as a debug user.");
                }
                else if(args[1].equalsIgnoreCase("list")) {
                    String[] debugUsers = ConfigEntry.DEBUG_USERS.getString().split(",");
                    channel.sendMessage(StringFormatter.parseLines(new String[] {"```lua", Arrays.toString(debugUsers), "```"})).queue();
                }
                else {
                    channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `debug add|remove|list [userid]`").queue();
                }
                break;

            case "announce":
                if(args.length < 2) {
                    channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `debug announce <announcement>`").queue();
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
}
