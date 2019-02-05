package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.listeners.PollListener;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.objects.Poll;
import me.mafrans.poppo.util.objects.Rank;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.text.ParseException;
import java.util.Date;

@Id("commands::poll")
public class Command_poll implements ICommand {

    @Override
    public String getName() {
        return "poll";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.UTILITY, "Starts a poll and DMs you the result. The 'end-date' argument is a UTC timestamp formatted as [" + Poll.DATE_FORMAT.toPattern() + "], ex. " + Poll.DATE_FORMAT.format(new Date()), "poll [end-date]", new String[] {"vote", "voteoff"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {

        if(Rank.requirePermission(command, Permission.MESSAGE_MANAGE)) {
            return true;
        }

        String[] args = command.getArgs();

        Date endDate = null;
        try {
            if (args.length > 0) {
                endDate = Poll.DATE_FORMAT.parse(args[0]);
            }
        }
        catch (ParseException ex) {
            channel.sendMessage("Argument [end-date] must be a UTC timestamp formatted as [" + Poll.DATE_FORMAT.toPattern() + "], ex. " + Poll.DATE_FORMAT.format(new Date())).queue();
            return false;
        }

        Poll poll = new Poll(command.getAuthor(), channel);
        poll.setEndDate(endDate);

        Poll.openPolls.add(poll);
        PollListener.waitMap.put(poll, PollListener.PollState.STATE_TITLE);
        command.getAuthor().openPrivateChannel().complete().sendMessage("Please enter a title for your poll. You can cancel your poll creation at any time by entering 'cancel'.").queue();

        return true;
    }
}
