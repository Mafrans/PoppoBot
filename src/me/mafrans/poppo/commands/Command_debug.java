package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.ConfigEntry;
import me.mafrans.poppo.util.TimerTasks;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;

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

            case "adddebuguser":
                if(!Arrays.asList(ConfigEntry.OVERLORD_USERS.getString().split(",")).contains(command.getAuthor().getId())) {
                    channel.sendMessage("You do not have permission to use this command.").queue();
                    break;
                }
                if(args.length != 2) {
                    channel.sendMessage("Correct usage for command " + command.getCmd() + " is: `debug run <class>`").queue();
                    break;
                }

                ConfigEntry.DEBUG_USERS.set(ConfigEntry.DEBUG_USERS.getString() + "," + args[1]);

                channel.sendMessage("Added " + args[1] + " as a debug user.");
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
