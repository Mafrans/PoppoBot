package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.StringFormatter;
import me.mafrans.poppo.util.TimerTasks;
import me.mafrans.poppo.util.config.DataUser;
import me.mafrans.poppo.util.config.SQLDataUser;
import me.mafrans.poppo.util.objects.Rank;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.ZoneOffset;
import java.util.*;

import static me.mafrans.poppo.Main.config;

@Id("commands::debug")
public class Command_debug implements ICommand {
    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(
                CommandCategory.UTILITY,
                "Runs debug scripts.",
                "debug <arguments>",
                null,
                false,
                true);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        if(!config.debug_users.contains(command.getAuthor().getId())) {
            return true;
        }
        String[] args = command.getArgs();

        switch(args[0].toLowerCase()) {
            case "help": {
                channel.sendMessage("debug updateusers\n" +
                        "debug run <runnable class>\n" +
                        "debuguser <put|remove> <userid>").queue();
                return true;
            }
            case "updateroles":
                if(args.length != 1) {
                    debugSendMessageAsync(channel, "Correct usage for command " + command.getCmd() + " is: `debug updateroles`");
                    return true;
                }
                for(Guild guild : Main.jda.getGuilds()) {
                    for(Rank rank : Rank.values()) {
                        System.out.println(rank.getRoleMap());
                        if(rank.getRole(guild) != null) {
                            rank.getRole(guild).delete().queue();
                        }

                        rank.initialize();
                    }
                }
                break;


            case "updateusers":
                if(args.length != 1) {
                    debugSendMessageAsync(channel, "Correct usage for command " + command.getCmd() + " is: `debug updateusers`");
                    return true;
                }
                for(User user : Main.jda.getUsers()) {
                    OnlineStatus onlineStatus = Main.jda.getMutualGuilds(user).get(0).getMember(user).getOnlineStatus();

                    if(Main.userList.getUsersFrom("uuid", user.getId()).size() == 0) {
                        System.out.println(String.format("[UpdateUsers] Creating new entry: %s", user.getName() + user.getDiscriminator()));
                        Main.userList.put(new SQLDataUser(new DataUser(
                                Collections.singletonList(user.getName()),
                                user.getId(),
                                onlineStatus == OnlineStatus.ONLINE || onlineStatus == OnlineStatus.DO_NOT_DISTURB || onlineStatus == OnlineStatus.IDLE  ? "Currently Online" : GUtil.currentParsedDate(ZoneOffset.UTC),
                                user.getAvatarUrl(), 0)));
                    }
                    else {
                        System.out.println(String.format("[UpdateUsers] Updating old entry: %s", user.getName() + user.getDiscriminator()));
                        DataUser dataUser = Main.userList.getUsersFrom("uuid", user.getId()).get(0);
                        if(!dataUser.getNames().contains(user.getName())) {
                            List<String> names = new ArrayList<>(dataUser.getNames());
                            names.add(user.getName());
                            dataUser.setNames(names);
                        }
                        if(dataUser.getLastOnlineTag().equals("Currently Online")) {
                            if(onlineStatus == OnlineStatus.OFFLINE || onlineStatus == OnlineStatus.INVISIBLE || onlineStatus == OnlineStatus.UNKNOWN) {
                                dataUser.setLastOnlineTag(GUtil.currentParsedDate(ZoneOffset.UTC));
                            }
                        }
                        Main.userList.put(new SQLDataUser(dataUser));
                    }
                }
                break;

            case "purgeself":
                if(args.length != 2) {
                    debugSendMessageAsync(channel, "Correct usage for command " + command.getCmd() + " is: `debug run <class>`");
                    return true;
                }

                for(Message message : channel.getHistory().retrievePast(Integer.parseInt(args[1])).complete()) {
                    if(message.getAuthor().getId().equals(Main.jda.getSelfUser().getId())) {
                        message.delete().queue();
                    }
                }
                break;

            case "run":
                if(args.length != 2) {
                    debugSendMessageAsync(channel, "Correct usage for command " + command.getCmd() + " is: `debug run <class>`");
                    return true;
                }
                Class<?> runClass;
                try {
                    runClass = Class.forName(args[1]);
                }
                catch (ClassNotFoundException | ClassCastException e) {
                    debugSendMessageAsync(channel, "Could not find a runnable class with the name of \"" + args[1] + ".java\"\n```" + e.getMessage() + "```");
                    break;
                }
                if(!(runClass.getDeclaredConstructor().newInstance() instanceof Runnable)) {
                    channel.sendMessage("Class is not runnable.").queue();
                    break;
                }
                Runnable instance = (Runnable) runClass.getDeclaredConstructor().newInstance();
                instance.run();
                debugSendMessageAsync(channel, "Successfully ran runnable class \"" + instance.getClass().getName() + "\"");
                break;

            case "debuguser":
                if(!config.overlord_users.contains(command.getAuthor().getId())) {
                    debugSendMessageAsync(channel, "You do not have permission to use this command.");
                    break;
                }
                if(args.length != 3) {
                    debugSendMessageAsync(channel, "Correct usage for command " + command.getCmd() + " is: `debug run <class>`");
                    break;
                }

                if(args[1].equalsIgnoreCase("put")) {
                    config.debug_users.add(args[2]);

                    debugSendMessageAsync(channel, "Added " + args[2] + " as a debug user.");
                }
                else if(args[1].equalsIgnoreCase("remove")) {
                    if(config.debug_users.contains(args[2])) {
                        debugSendMessageAsync(channel, "Added `" + args[2] + "` as a debug user.");
                    }
                    else {
                        debugSendMessageAsync(channel, "Could not find a debug user with the id: `" + args[2] + "`");
                    }

                    config.debug_users.add(args[2]);

                    channel.sendMessage("Added " + args[1] + " as a debug user.").queue();
                }
                else if(args[1].equalsIgnoreCase("list")) {
                    String[] debugUsers = config.debug_users.toArray(new String[0]);
                    debugSendMessageAsync(channel, StringFormatter.parseLines(new String[] {"```lua", Arrays.toString(debugUsers), "```"}));
                }
                else {
                    debugSendMessageAsync(channel, "Correct usage for command " + command.getCmd() + " is: `debug put|remove|list [userid]`");
                }
                break;

            case "announce":
                if(args.length < 2) {
                    debugSendMessageAsync(channel, "Correct usage for command " + command.getCmd() + " is: `debug announce <announcement>`");
                    break;
                }

                for(Guild guild : Main.jda.getGuilds()) {
                    PrivateChannel privateChannel = guild.getOwner().getUser().openPrivateChannel().complete();
                    if(args[1].equalsIgnoreCase("-rm")) {
                        if(!NumberUtils.isDigits(args[2])) {
                            channel.sendMessage("Argument -rm requires a value: <time>").queue();
                            break;
                        }

                        int time = Integer.parseInt(args[2]);
                        try {
                            final Message message = privateChannel.sendMessage(StringUtils.join(ArrayUtils.subarray(args, 3, args.length), " ")).complete();

                            TimerTasks.queueTask(() -> message.delete().queue(), time);
                        }
                        catch(IndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }

                        break;
                    }
                    privateChannel.sendMessage(StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ")).complete();
                }
        }
        return true;
    }

    private void debugSendMessageAsync(TextChannel channel, String message) {
        channel.sendMessage("```lua\n" + message + "\n```").queue();
    }
}
