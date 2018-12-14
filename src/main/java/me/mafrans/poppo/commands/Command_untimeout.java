package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.objects.Rank;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

public class Command_untimeout implements ICommand {
    @Override
    public String getName() {
        return "untimeout";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.MODERATION, "Removes a timeout from a user.", "untimeout <user>", new ArrayList<>(), false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        if(args.length != 1) return false;

        String uuid;
        if (args[0].length() == 18 && NumberUtils.isDigits(args[0])) {
            uuid = args[0];
        } else if (command.getMessage().getMentionedUsers().size() > 0) {
            uuid = command.getMessage().getMentionedUsers().get(0).getId();
        } else if (Main.jda.getUsersByName(StringUtils.join(args, " "), true).size() > 0) {
            uuid = Main.jda.getUsersByName(StringUtils.join(args, " "), true).get(0).getId();
        } else {
            channel.sendMessage("Could not find a user with that name or id.").queue();
            return true;
        }

        User user = Main.jda.getUserById(uuid);
        if (user == null) {
            channel.sendMessage("Could not find a user with that name or id.").queue();
            return true;
        }

        channel.getGuild().getController().removeRolesFromMember(channel.getGuild().getMember(user), Rank.TIMED_OUT.getRole(channel.getGuild())).queue();
        if(!user.isBot()) {
            user.openPrivateChannel().complete().sendMessage("You are no longer timed out in **" + channel.getGuild().getName() + "**").queue();
        }
        channel.sendMessage(user.getAsMention() + " is no longer timed out.").queue();

        return true;
    }
}
